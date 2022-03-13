package com.alaeri.presentation.tui

import com.alaeri.domain.wiki.WikiText
import com.alaeri.log
import com.alaeri.presentation.ContentPanelState
import com.alaeri.presentation.PresentationState
import com.alaeri.presentation.tui.wrap.LineWrapper
import com.alaeri.presentation.wiki.PanelSizes
import com.alaeri.presentation.wiki.SelectionRepository
import com.googlecode.lanterna.SGR
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.Terminal
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class TerminalAppScreen(private val terminal: Terminal,
                        private val screen: Screen,
                        private val viewModelFactory: IViewModelFactory,
                        private val drawCoroutineContext : CoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher(),
                        private val readKeyCoroutineContext : CoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {

    private var mainContentPanel: Panel
    private var lastRowPanel: Panel
    private var rootWindow: MultiWindowTextGUI
    private val textBox: TextBox
    private val mainContentPanelLeft: Panel
    private val previewPanel: Panel
    private val lineWrapper = LineWrapper()

    init {
        screen.startScreen()
        rootWindow = MultiWindowTextGUI(screen).apply {

        }
        val browseWindow = BasicWindow("Browse").apply {
            setHints(listOf(Window.Hint.FULL_SCREEN,
                Window.Hint.NO_DECORATIONS,
                Window.Hint.FIT_TERMINAL_WINDOW))
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
        mainContentPanel = Panel(gridLayout).apply {
            addTo(windowPanel)
            preferredSize = parent.preferredSize.withRelativeRows(-1)
        }
        mainContentPanelLeft = Panel(AbsoluteLayout()).apply {
            addTo(mainContentPanel)
            preferredSize = parent.preferredSize.withColumns(parent.preferredSize.columns / 2)
        }
        previewPanel = Panel(AbsoluteLayout()).apply {
            addTo(mainContentPanel)
            preferredSize = parent.preferredSize.withColumns(parent.preferredSize.columns / 2)
        }
//        mainContentPanel.addComponent(Label("TEST2").apply {
//            addTo(mainContentPanel)
//            preferredSize = parent.preferredSize.withColumns(parent.preferredSize.columns / 2)
//        })
        val lastRowLayout = LinearLayout(Direction.HORIZONTAL)
        lastRowPanel = Panel(lastRowLayout).apply {
            addTo(windowPanel)
            preferredSize = parent.preferredSize.withRows(1)
        }
        windowPanel.addComponent(
            lastRowPanel,
            LinearLayout.createLayoutData(LinearLayout.Alignment.End)
        )
        lastRowPanel.addComponent(Label("search"))
        textBox = TextBox().apply {
            setTextChangeListener { newText, changedByUserInteraction ->

            }
        }
        lastRowPanel.addComponent(textBox)
        rootWindow.updateScreen()
    }

    suspend fun updateScreen(combined: PresentationState.Presentation) = log("update screen"){
        val terminalSize = screen.doResizeIfNecessary()
        if(terminalSize != null){
            rootWindow.activeWindow.component.apply {
                preferredSize = screen.terminalSize
            }
            mainContentPanel.apply {
                preferredSize = parent.preferredSize.withRelativeRows(-1)
            }
            mainContentPanelLeft.apply {
                preferredSize =
                    parent.preferredSize.withColumns(parent.preferredSize.columns / 2)
            }
            previewPanel.apply {
                preferredSize =
                    parent.preferredSize.withColumns(parent.preferredSize.columns / 2)
            }
            lastRowPanel.apply {
                preferredSize = parent.preferredSize.withRows(1)
            }
            textBox.apply {

            }
        }

        println("updating screen: hasResize: $terminalSize")
        val inputState = combined.inputState
        val contentStatus = combined.contentStatus
        val selectedWikiText = combined.selectedWikiText
        val previewStatus = combined.previewStatus
        textBox.text = inputState.text
        println("updating screen1")
        printPage(mainContentPanelLeft, contentStatus, selectedWikiText)
//        when (contentStatus) {
//            is LoadingStatus.Done -> {
//                //data class CursorPosStart(val x: Int, val y: Int)
//                //contentStatus.result.lines.forEach { logger?.println(it) }
//                //val textGraphics = screen.newTextGraphics()
//
//            }
//            is LoadingStatus.Empty -> {
//                val message = when(contentStatus.emptyStatusReason){
//                    EmptyStatusReason.NotInitialized -> {
//                        WikiArticle(
//                            "",
//                            "",
//                            mutableListOf(
//                                mutableListOf<WikiText>(
//                                    WikiText.NormalText(
//                                        "Select something to see it appear here"
//                                    )
//                                )
//                            )
//                        )
//                    }
//                }
//                printPage(mainContentPanelLeft, LoadingStatus.Done(message))
//            }
//            else -> {
//                val fakePage = WikiArticle(
//                    "",
//                    "",
//                    mutableListOf(
//                        mutableListOf<WikiText>(
//                            WikiText.NormalText(
//                                contentStatus.toString()
//                            )
//                        )
//                    )
//                )
//                printPage(mainContentPanelLeft, LoadingStatus.Done(fakePage))
//            }
//        }
        println("updating screen2")
        printPage(previewPanel, previewStatus, selectedWikiText)
//        when (previewStatus) {
//            is LoadingStatus.Done -> {
//                //data class CursorPosStart(val x: Int, val y: Int)
//                //previewStatus.result.lines.forEach { logger?.println(it) }
//                //val textGraphics = screen.newTextGraphics()
//
//            }
//            is LoadingStatus.Empty -> {
//                val message = when(previewStatus.emptyStatusReason){
//                    EmptyStatusReason.NotInitialized -> {
//                        WikiArticle(
//                            "",
//                            "",
//                            mutableListOf(
//                                mutableListOf<WikiText>(
//                                    WikiText.NormalText(
//                                        "Select something to see it appear here"
//                                    )
//                                )
//                            )
//                        )
//
//                    }
//                }
//                printPage(previewPanel, LoadingStatus.Done(message))
//            }
//            else -> {
//                val fakePage = WikiArticle(
//                    "",
//                    "",
//                    mutableListOf(
//                        mutableListOf<WikiText>(
//                            WikiText.NormalText(
//                                previewStatus.toString()
//                            )
//                        )
//                    )
//                )
//                printPage(previewPanel, LoadingStatus.Done(fakePage))
//            }
//        }
        println("updating screen2bis")

        println("updating screen3")
        try {
            rootWindow.updateScreen()
        }catch (e: Throwable){
            println("error updating: $e")
        }

        println("screen updated")



    }

    private val keyFlow: Flow<KeyStroke> = flow<KeyStroke> {
        val emissionContext = currentCoroutineContext()
        withContext(readKeyCoroutineContext) {
            var keyStroke = screen.readInput()
            while (true) {
                println("KeyStroke: $keyStroke")
                withContext(emissionContext) {
                    println("emitKeyStroke: $keyStroke $emissionContext")
                    emit(keyStroke)
                }
                keyStroke = screen.readInput()
            }
        }
    }

    private val sizeFlow: Flow<PanelSizes> = callbackFlow<PanelSizes> {
        println("instantiate resize")
        trySend(PanelSizes(mainContentPanelLeft.size, previewPanel.size))
        terminal.addResizeListener { _, newSize ->


            //.apply {
            //
            //
            //            }
            val panelSize = newSize.withColumns(newSize.columns/2).withRelativeRows(-1)
            trySend(PanelSizes(panelSize, panelSize))
        }
        awaitClose {
            //terminal.removeResizeListener() }
        }
    }.onEach { println("size: $it") }

    private fun printPage(
        mainContentPanelLeft: Panel,
        contentStatus: ContentPanelState,
        selectedPosition: SelectionRepository.Selection? = null
    ) {
        mainContentPanelLeft.removeAllComponents()
        val maxColumns = mainContentPanelLeft.size.columns
        val maxHeight = mainContentPanelLeft.size.rows
        printV1(
            contentStatus,
            maxColumns,
            maxHeight,
            mainContentPanelLeft,
            selectedPosition
        )
        //printV2(contentStatus, maxColumns, maxHeight, mainContentPanelLeft)

    }

    private fun printV1(
        contentStatus: ContentPanelState,
        maxColumns: Int,
        maxHeight: Int,
        mainContentPanelLeft: Panel,
        selectedWikiText: SelectionRepository.Selection?
    ) {
        contentStatus.lines.forEach { line ->
            line.chunks.filter { it.position.row < maxHeight }.forEach {
                val label = Label(it.text.text).apply {
                    position = it.position
                    size = TerminalSize(it.text.text.length, 1)
                    if (it.text is WikiText.InternalLink) {
                        addStyle(SGR.BOLD)
                        if (it.text.target == selectedWikiText?.content?.target) {
                            addStyle(SGR.REVERSE)
                        }
                    }
                }
                mainContentPanelLeft.addComponent(label)
            }
        }
    }

    suspend fun runAppAndWait()  = log(name = "run and wait"){
        var executionJob: Job? = null
        supervisorScope {
            try{
                executionJob = launch {
                    val instantiationScope = this
                    val sharedTerminalScreen =  SharedTerminalScreen(
                        this@TerminalAppScreen.keyFlow,
                        this@TerminalAppScreen.sizeFlow,
                        instantiationScope)

                    val viewModel = viewModelFactory.provideViewModel(
                        sharedTerminalScreen,
                        instantiationScope
                    )
                    val screenStateFlow = viewModel.screenState
                    instantiationScope.launch {
                        withContext(Dispatchers.IO){
                            viewModel.startProcessingKeyStrokes()
                        }
                    }
                    screenStateFlow.flowOn(drawCoroutineContext).collect {
                        when(it){
                            is PresentationState.Presentation -> this@TerminalAppScreen.updateScreen(it)
                            is PresentationState.Loading -> {}
                            else -> executionJob?.cancelAndJoin()
                        }
                    }

                }
                executionJob?.join()
            }catch (e: Exception){
                println("screen will stop after this")
            }
            finally {
                screen.stopScreen()
            }
        }
    }
}
