package com.alaeri.tui_browser

import com.alaeri.tui_browser.wiki.LoadingStatus
import com.alaeri.tui_browser.wiki.WikiArticle
import com.alaeri.tui_browser.wiki.WikiRepository
import com.alaeri.tui_browser.wiki.WikiText
import com.googlecode.lanterna.SGR
import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.Executors
import kotlin.system.exitProcess


object TuiBrowser {

    val drawCoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    val readKeyCoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    sealed class BrowserException(message: String, cause: Exception? = null): Exception(message, cause){
        data class InvalidInput(val keyStroke: KeyStroke, val state: InputState): BrowserException("invalid input $keyStroke for state: $state")
        object CaretAtStart: BrowserException("stop pressing on backspace it's useless")
        object NothingToSearch: BrowserException("nothing to search")
    }
    data class InputState(val text: String, val searchTerm : String? = null, val error: BrowserException? = null)
    data class CombinedState(val inputState: InputState, val contentStatus: LoadingStatus, val selectedWikiText: WikiText.InternalLink?)

    private val logger: ILogger? = null

    @JvmStatic
    fun main(args: Array<String>) {
        if(false){
            //println("".padStart(3, 'A')+"coucou")
            val list = mutableListOf<WikiText>(WikiText.NormalText("MARCEL"), WikiText.NormalText("Philippe"), WikiText.NormalText("BRICE"))
            //println(wrap(TerminalPosition(0,0), WikiText.NormalText("coucou"), 10))
            //println(wrap(TerminalPosition(8,0), WikiText.NormalText("coucou"), 10))
            val res = chunkAndWrapElements(list, TerminalPosition(0,0), 10)
            //println(res)
            "MARCELPhilippeBrice".chunked(10).forEach { println(it) }
        }else{
            setupTerminalApp()
        }





        // terminal.exitPrivateMode()
//        rootWindow.addWindowAndWait(browseWindow)
    }

    private fun setupTerminalApp() {
        val screen = DefaultTerminalFactory().createScreen().apply {
            startScreen()

        }

        val rootWindow = MultiWindowTextGUI(screen).apply {

        }

        val browseWindow = BasicWindow("Browse").apply {
            setHints(listOf(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS))
            rootWindow.addWindow(this)

        }

        //browseWindow.textGUI = rootWindow
        val linearLayout = LinearLayout()
        val windowPanel = Panel(linearLayout).apply {
            preferredSize = screen.terminalSize

        }
        browseWindow.component = windowPanel
        val gridLayout = GridLayout(2).apply {
        }
        val mainContentPanel = Panel(gridLayout).apply {
            addTo(windowPanel)
            preferredSize = parent.preferredSize.withRelativeRows(-1)
        }
        val mainContentPanelLeft = Panel(AbsoluteLayout()).apply {
            addTo(mainContentPanel)
            preferredSize = parent.preferredSize.withColumns(parent.preferredSize.columns / 2)
        }
        mainContentPanel.addComponent(Label("TEST2").apply {
            addTo(mainContentPanel)
            preferredSize = parent.preferredSize.withColumns(parent.preferredSize.columns / 2)
        })
        val lastRowLayout = LinearLayout(Direction.HORIZONTAL)
        val lastRowPanel = Panel(lastRowLayout).apply {
            addTo(windowPanel)
            preferredSize = parent.preferredSize.withRows(1)
        }
        windowPanel.addComponent(
            lastRowPanel,
            LinearLayout.createLayoutData(LinearLayout.Alignment.End)
        )
        lastRowPanel.addComponent(Label("search"))
        val textBox = TextBox().apply {
            setTextChangeListener { newText, changedByUserInteraction ->

            }
        }
        lastRowPanel.addComponent(textBox)
        val wikiRepository = WikiRepository(logger)

        val keyFlow = flow<KeyStroke> {
            val emissionContext = currentCoroutineContext()
            withContext(readKeyCoroutineContext) {
                var keyStroke = screen.readInput()
                while (true) {
                    withContext(emissionContext) {
                        emit(keyStroke)
                    }
                    keyStroke = screen.readInput()
                }
            }
        }
        rootWindow.updateScreen()
        runBlocking {
            val sharedKeyFlow = keyFlow.shareIn(this, SharingStarted.Lazily)
            val inputStateFlow = sharedKeyFlow.scan(InputState("")) { acc, keyStroke ->
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
                    char != null && !char.isWhitespace() -> acc.copy(
                        text = acc.text + char,
                        error = null
                    )
                    else -> acc.copy(error = BrowserException.InvalidInput(keyStroke, acc))
                }
            }

            val job = launch {
                val sharedInput =
                    inputStateFlow.shareIn(this, SharingStarted.WhileSubscribed(), replay = 0)
                val searchState = sharedInput.map { it.searchTerm }
                    .distinctUntilChanged()
                    .flatMapLatest { wikiRepository.loadWikiArticle(it) }
                    .conflate()
                    .shareIn(this, SharingStarted.WhileSubscribed())
                val selectableElements = searchState
                        .map { it as? LoadingStatus.Done }
                        .map { done -> done?.result?.lines?.flatMap { line -> line.mapNotNull { it as? WikiText.InternalLink } } ?: listOf() }
                        .onStart { emit(listOf()) }
                val selectedWikiText = selectableElements.flatMapLatest { selectables ->
                    if(selectables.isEmpty()){
                        flowOf<WikiText.InternalLink?>(null)
                    }else{
                        keyFlow.filter { it.keyType == KeyType.Tab }.scan<KeyStroke, WikiText.InternalLink?>(null){ selected, keyStroke ->
                            if(selected != null){
                                val index = selectables.indexOf(selected)
                                selectables[ index + 1 % selectables.size]
                            }else{
                                selectables.firstOrNull()
                            }
                        }
                    }
                }.onEach {  println("HO") }
                val mergedFlow: Flow<CombinedState> =
                    combine(
                        sharedInput.onEach { println("sharedInput") },
                        searchState.onEach { println("searchState") },
                        selectedWikiText.onEach { println("selection") }) {
                            input, search, selected -> CombinedState(input, search, selected)
                    }
                mergedFlow.flowOn(drawCoroutineContext).collect { combined ->
                    val inputState = combined.inputState
                    val contentStatus = combined.contentStatus
                    val selectedWikiText = combined.selectedWikiText
                    println("combined")

                    textBox.text = inputState.text

                    when (contentStatus) {
                        is LoadingStatus.Done -> {
                            //data class CursorPosStart(val x: Int, val y: Int)
                            contentStatus.result.lines.forEach { logger?.println(it) }
                            //val textGraphics = screen.newTextGraphics()
                            printPage(mainContentPanelLeft, contentStatus, selectedWikiText)
                        }
                        else -> {
                            val fakePage = WikiArticle(
                                "",
                                "",
                                mutableListOf(
                                    mutableListOf<WikiText>(
                                        WikiText.NormalText(
                                            contentStatus.toString()
                                        )
                                    )
                                )
                            )
                            printPage(mainContentPanelLeft, LoadingStatus.Done(fakePage))
                        }
                    }
                    rootWindow.updateScreen()
                }
            }
           try {
               sharedKeyFlow.collect {
                   when(it.keyType){
                       KeyType.EOF, KeyType.Escape -> {
                           job.cancel()
                           screen.stopScreen()
                           logger?.println("bye")
                           exitProcess(0)
                       }
                   }
               }
           } catch (e: Exception){
               logger?.println(e)
           }
        }
    }

    data class ChunkedTextLine(
        val text: WikiText,
        val position: TerminalPosition,
        val end: TerminalPosition
    )

    private fun printPage(
        mainContentPanelLeft: Panel,
        contentStatus: LoadingStatus.Done,
        selectedPosition: WikiText.InternalLink? = null
    ) {
        mainContentPanelLeft.removeAllComponents()
        val maxColumns = mainContentPanelLeft.size.columns
        val maxHeight = mainContentPanelLeft.size.rows
        printV1(contentStatus, maxColumns, maxHeight, mainContentPanelLeft, selectedPosition)
        //printV2(contentStatus, maxColumns, maxHeight, mainContentPanelLeft)

    }

    private fun printV1(
        contentStatus: LoadingStatus.Done,
        maxColumns: Int,
        maxHeight: Int,
        mainContentPanelLeft: Panel,
        selectedWikiText: WikiText.InternalLink?
    ) {
        val startPos = TerminalPosition(0, 0)
        val endPos = TerminalPosition(maxColumns, maxHeight)
        contentStatus.result.lines.fold(startPos) { lineStartPos, line ->
            if (lineStartPos.row < endPos.row) {
                val result = chunkAndWrapElements(line, lineStartPos, maxColumns)
                result.chunks.filter { it.position.row < maxHeight }.forEach {
                    val label = Label(it.text.text).apply {
                        position = it.position
                        size = TerminalSize(it.text.text.length, 1)
                        if (it.text is WikiText.InternalLink) {
                            addStyle(SGR.BOLD)
                            if(it.text.target == selectedWikiText?.target){
                                addStyle(SGR.REVERSE)
                            }
                        }
                    }
                    mainContentPanelLeft.addComponent(label)
                }
                TerminalPosition(0, result.lastPosition.row + 1)
            } else {
                lineStartPos
            }
        }
    }

    private fun printV2(
        contentStatus: LoadingStatus.Done,
        maxColumns: Int,
        maxHeight: Int,
        mainContentPanelLeft: Panel
    ) {
        val wrappedLines = contentStatus.result.lines.flatMap { line ->
            data class CharAndElement(val char: Char, val element: WikiText)
            data class LineChunkAndElement(val chars: String, val element: WikiText)

            val paragraphLines = line.flatMap { element ->
                element.text.toCharArray().asList().map { CharAndElement(it, element) }
            }.chunked(maxColumns).map { listChars ->
                listChars.fold(listOf<LineChunkAndElement>()) { acc: List<LineChunkAndElement>, charAndElement ->
                    if (acc.isEmpty()) {
                        listOf(
                            LineChunkAndElement(
                                charAndElement.char.toString(),
                                charAndElement.element
                            )
                        )
                    } else {
                        val last = acc.last()
                        if (last.element == charAndElement.element) {
                            acc.subList(
                                0,
                                acc.size - 1
                            ) + listOf(
                                LineChunkAndElement(
                                    last.chars + charAndElement.char,
                                    last.element
                                )
                            )
                        } else {
                            acc + listOf(
                                LineChunkAndElement(
                                    charAndElement.char.toString(),
                                    charAndElement.element
                                )
                            )
                        }
                    }
                }
            }
            paragraphLines
        }
        var countedButton = 0
        wrappedLines.forEachIndexed { rowIndex, line ->
            if (rowIndex < maxHeight) {
                line.fold(0) { colIndex, it ->
                    val label = Label(it.chars).apply {
                        position = TerminalPosition(colIndex, rowIndex)
                        size = TerminalSize(it.chars.length, 1)
                        if (it.element is WikiText.InternalLink) {
                            addStyle(SGR.BOLD)
                            countedButton++
                            addStyle(SGR.REVERSE)
                        }
                    }
                    mainContentPanelLeft.addComponent(label)
                    colIndex + it.chars.length
                }
            } else {
                //
            }
        }
    }

    private fun chunkAndWrapElements(
        line: MutableList<WikiText>,
        lineStartPos: TerminalPosition,
        maxColumns: Int
    ): AccChunks {
        return line.fold(AccChunks(lineStartPos, listOf())) { acc, wikiText ->
//            val startPosition = if (acc.lastPosition.row >= maxColumns) {
//                TerminalPosition(0, acc.lastPosition.row + 1)
//            } else {
//                acc.lastPosition
//            }
            val chunks = wrap(acc.lastPosition, wikiText, maxColumns)
            acc.copy(
                lastPosition = TerminalPosition(
                    chunks.last().end.column,
                    chunks.last().end.row
                ),
                chunks = acc.chunks + chunks
            )
        }
    }

    data class AccChunks(val lastPosition: TerminalPosition, val chunks: List<ChunkedTextLine>)

    private fun wrap(
        textStartPos: TerminalPosition,
        wikiText: WikiText,
        maxColumns: Int
    ): List<ChunkedTextLine> {
        val padLength = textStartPos.column
        val paddedText = "".padStart(padLength, '-') + wikiText.text
        //println(paddedText)
        return paddedText.chunked(maxColumns).mapIndexed { index, chunk ->
            val startCol = if (index == 0) {
                textStartPos.column
            } else {
                0
            }
            val unpaddeddChunk = if(index == 0){
               chunk.slice(padLength until chunk.length)
            }else{
                chunk.trimStart()
            }
            val mappedWikiText = when(wikiText){
                is WikiText.InternalLink -> WikiText.InternalLink(unpaddeddChunk, wikiText.target)
                else -> WikiText.NormalText(unpaddeddChunk)
            }
            val startPos = TerminalPosition(startCol, textStartPos.row + index)
            val endPos = TerminalPosition(startCol + unpaddeddChunk.length, startPos.row)
            ChunkedTextLine(mappedWikiText, startPos, endPos)
        }
    }






}


