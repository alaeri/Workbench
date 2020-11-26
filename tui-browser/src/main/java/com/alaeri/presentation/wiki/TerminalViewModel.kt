package com.alaeri.presentation.wiki

import com.alaeri.presentation.tui.ITerminalScreen
import com.alaeri.presentation.tui.ITerminalViewModel
import com.alaeri.domain.wiki.BrowserException
import com.alaeri.domain.ILogger
import com.alaeri.presentation.InputState
import com.alaeri.presentation.PresentationState
import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiRepository
import com.alaeri.domain.wiki.WikiText
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class TerminalViewModel(
    private val initializationScope: CoroutineScope,
    private val terminalScreen: ITerminalScreen,
    private val wikiRepository: WikiRepository,
    private val logger: ILogger
): ITerminalViewModel {


    override val screenState: Flow<PresentationState>

    private val mutableSharedFlow = MutableSharedFlow<InputState>()

    init {
        val keyFlow = terminalScreen.keyFlow
        val inputStateFlow = keyFlow.scan(InputState("")) { acc, keyStroke ->
            val currentQueryLength = acc.text.length
            val char = keyStroke.character
            val keyType = keyStroke.keyType
            return@scan when {
                keyType == KeyType.Backspace -> if (currentQueryLength > 0) {
                    val slicedText = acc.text.slice(0 until currentQueryLength - 1)
                    acc.copy(text = slicedText, error = null)
                } else {
                    acc.copy(text = "", error = BrowserException.CaretAtStart)
                }
                keyType == KeyType.Enter -> if (currentQueryLength > 0) {
                    InputState("", acc.text, null)
                } else {
                    acc.copy(error = BrowserException.NothingToSearch)
                }
                keyType == KeyType.ArrowRight ||
                keyType == KeyType.Tab -> acc
                char != null && !char.isWhitespace() -> acc.copy(
                    text = acc.text + char,
                    error = null
                )
                else -> acc.copy(error = BrowserException.InvalidInput(keyStroke, acc))
            }
        }.distinctUntilChanged().shareIn(initializationScope, SharingStarted.WhileSubscribed(), replay = 0)

        val searchState = mutableSharedFlow.map { it.searchTerm }
            .distinctUntilChanged()
            .flatMapLatest { wikiRepository.loadWikiArticle(it) }
            .conflate()
            .shareIn(initializationScope, SharingStarted.WhileSubscribed())
        val selectableElements = searchState
            .map { it as? LoadingStatus.Done }
            .map { done ->
                done?.result?.lines?.flatMap { line -> line.mapNotNull { it as? WikiText.InternalLink } }
                    ?: listOf()
            }
            .onStart { emit(listOf()) }
        val selectedWikiText = selectableElements.flatMapLatest { selectables ->
            if (selectables.isEmpty()) {
                flowOf<WikiText.InternalLink?>(null)
            } else {
                keyFlow.filter { it.keyType == KeyType.Tab }
                    .scan<KeyStroke, WikiText.InternalLink?>(null) { selected, keyStroke ->
                        if (selected != null) {
                            val index = selectables.indexOf(selected)
                            selectables[index + 1 % selectables.size]
                        } else {
                            selectables.firstOrNull()
                        }
                    }
            }
        }.onEach { logger.println("HO") }.shareIn(initializationScope, SharingStarted.Lazily)

        val launchSearch = combine(selectedWikiText, keyFlow) { selected, keyStroke ->
            if (selected != null && keyStroke.keyType == KeyType.ArrowRight) {
                InputState("", selected.target, null)
            } else {
                null
            }
        }.filterNotNull()


        screenState = keyFlow.map { it.keyType != KeyType.EOF && it.keyType != KeyType.Escape }
            .distinctUntilChanged().flatMapLatest {
                if (it) {
                    combine(
                        mutableSharedFlow.onEach { logger.println("sharedInput") },
                        searchState.onEach { logger.println("searchState") },
                        selectedWikiText.onEach { logger.println("selection") }) { input, search, selected ->
                        PresentationState.Presentation(input, search, selected)
                    }
                } else {
                    flowOf(PresentationState.Exit(listOf()))
                }
            }

        initializationScope.launch {
            merge(inputStateFlow, launchSearch).onEach { logger.println("mergedInput: $it") }.collect { mutableSharedFlow.emit(it) }
        }

    }


}