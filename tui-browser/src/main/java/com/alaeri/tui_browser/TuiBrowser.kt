package com.alaeri.tui_browser

import com.alaeri.tui_browser.wiki.LoadingStatus
import com.alaeri.tui_browser.wiki.WikiRepository
import com.alaeri.tui_browser.wiki.WikiText
import com.googlecode.lanterna.SGR
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.Executors


object TuiBrowser {

    val drawCoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    val readKeyCoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    sealed class BrowserException(message: String, cause: Exception? = null): Exception(message, cause){
        data class InvalidInput(val keyStroke: KeyStroke, val state: InputState): BrowserException("invalid input $keyStroke for state: $state")
        object CaretAtStart: BrowserException("stop pressing on backspace it's useless")
        object NothingToSearch: BrowserException("nothing to search")
    }
    data class InputState(val text: String, val searchTerm : String? = null, val error: BrowserException? = null)
    data class CombinedState(val inputState: InputState, val contentStatus: LoadingStatus)


    @JvmStatic
    fun main(args: Array<String>) {
        val terminal = DefaultTerminalFactory().createTerminal().apply {
            enterPrivateMode()

        }
        val wikiRepository = WikiRepository()

        val keyFlow = flow<KeyStroke> {
            val emissionContext = currentCoroutineContext()
            withContext(readKeyCoroutineContext) {
                var keyStroke = terminal.readInput()
                while(keyStroke.keyType != KeyType.Escape) {
                    withContext(emissionContext) {
                        emit(keyStroke)
                    }
                    keyStroke = terminal.readInput()
                }
            }
        }
        val inputStateFlow = keyFlow.scan(InputState("")){ acc, keyStroke ->
            val currentQueryLength = acc.text.length
            val char = keyStroke.character
            val keyType = keyStroke.keyType
            return@scan when{
                keyType == KeyType.Backspace -> if(currentQueryLength > 0){
                    val slicedText = acc.text.slice(0 until currentQueryLength-1)
                        acc.copy(text = slicedText, error = null)
                    }else {
                        acc.copy(text = "", error = BrowserException.CaretAtStart)
                    }
                keyType == KeyType.Enter -> if(currentQueryLength > 0){
                    InputState("", acc.text, null)
                }else{
                    acc.copy(error = BrowserException.NothingToSearch)
                }
                char != null && !char.isWhitespace() -> acc.copy(text = acc.text+char, error = null)
                else -> acc.copy(error = BrowserException.InvalidInput(keyStroke, acc))
            }
        }

        runBlocking {
            val sharedInput = inputStateFlow.shareIn(this, SharingStarted.WhileSubscribed(), replay = 0)
            val searchState = sharedInput.map { it.searchTerm }.distinctUntilChanged()
                .flatMapLatest { wikiRepository.loadWikiArticle(it) }.conflate()
            val mergedFlow : Flow<CombinedState> = combine(sharedInput, searchState){ input, search ->
                CombinedState(input, search)
            }
            mergedFlow.collect { combined ->
                val inputState = combined.inputState
                val contentStatus = combined.contentStatus

                val textGraphics = terminal.newTextGraphics();

                textGraphics.drawLine(2, 0, terminal.terminalSize.columns - 1, 0, ' ');
                textGraphics.drawLine(2, 1, terminal.terminalSize.columns - 1, 1, ' ');
                textGraphics.drawLine(2, 2, terminal.terminalSize.columns - 1, 2, ' ');

                when(contentStatus) {
                    is LoadingStatus.Done -> {
                        data class CursorPosStart(val x: Int, val y: Int)
                        contentStatus.result.lines.forEach { println(it) }
                        contentStatus.result.lines.take(2).forEachIndexed { index, line ->
                            line.fold(CursorPosStart(2, index)){
                                acc, wikiText ->
                                when(wikiText){
                                    is WikiText.InternalLink -> textGraphics.putString(acc.x, acc.y, wikiText.text, SGR.BOLD);
                                    else -> textGraphics.putString(acc.x, acc.y, wikiText.text);
                                }
                                acc.copy(x = acc.x + wikiText.text.length)
                            }
                        }
                    }
                    else -> {
                        textGraphics.putString(2, 1, contentStatus.toString(), SGR.BOLD);
                    }
                }

                if(inputState.searchTerm != null){
                    textGraphics.putString(5, 3, "loading: ", SGR.BOLD);
                    textGraphics.putString(5 + "loading: ".length, 3, inputState.searchTerm);
                }

                textGraphics.drawLine(5, 3, terminal.terminalSize.columns - 1, 3, ' ');
                if(inputState.searchTerm != null){
                    textGraphics.putString(5, 3, "loading: ", SGR.BOLD);
                    textGraphics.putString(5 + "loading: ".length, 3, inputState.searchTerm);
                }
                textGraphics.drawLine(5, 4, terminal.terminalSize.columns - 1, 4, ' ');
                textGraphics.putString(5, 4, "search: ", SGR.BOLD);
                textGraphics.putString(5 + "search: ".length, 4, inputState.text);
                textGraphics.drawLine(5, 5, terminal.terminalSize.columns - 1, 5, ' ');
                if(inputState.error != null){
                    terminal.bell()
                    textGraphics.putString(5, 5, inputState.error.message);
                }
                terminal.setCursorPosition(5+ "search: ".length+inputState.text.length, 4)
                terminal.setCursorVisible(true)
                terminal.flush();
            }
        }
        terminal.exitPrivateMode()
    }


}


