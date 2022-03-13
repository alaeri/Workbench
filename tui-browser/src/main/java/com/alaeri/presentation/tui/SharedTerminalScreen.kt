package com.alaeri.presentation.tui

import com.alaeri.presentation.wiki.PanelSizes
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.input.KeyStroke
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class SharedTerminalScreen(_keyFlow: Flow<KeyStroke>,
                           _sizeFlow: Flow<PanelSizes>,
                           private val instantiationScope: CoroutineScope,
                           private val processKeyStrokeCoroutineContext: CoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
): ITerminalScreen {

    val shareScope = CoroutineScope(processKeyStrokeCoroutineContext)

    override val keyFlow: Flow<KeyStroke> = //sharedFlowCommand(initializationScope) {
        _keyFlow.shareIn(
            shareScope,
            replay = 1,
            started = SharingStarted.Lazily
        ).onEach { println("sharedKeyFlow: $it") }.buffer(10)
    //}

    override val sizeFlow: Flow<PanelSizes> = _sizeFlow.shareIn(shareScope,
        SharingStarted.Eagerly,
        1
    )

}