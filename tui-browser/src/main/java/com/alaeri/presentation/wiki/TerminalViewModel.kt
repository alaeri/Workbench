package com.alaeri.presentation.wiki

import com.alaeri.command.*
import com.alaeri.command.core.flow.FlowCommand
import com.alaeri.command.core.flow.flowCommand
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.command.core.suspend.suspendInvokeFlow
import com.alaeri.presentation.tui.ITerminalScreen
import com.alaeri.presentation.tui.ITerminalViewModel
import com.alaeri.domain.ILogger
import com.alaeri.presentation.InputState
import com.alaeri.presentation.PresentationState
import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiRepository
import com.alaeri.domain.wiki.WikiText
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class TerminalViewModel(
    private val initializationScope: CoroutineScope,
    private val terminalScreen: ITerminalScreen,
    private val wikiRepository: WikiRepository,
    private val logger: ILogger,
    private val commandLogger: DefaultIRootCommandLogger
): ITerminalViewModel, ICommandRootOwner {


    override val commandRoot = buildCommandRoot(this, "instantiation root", CommandNomenclature.Root, commandLogger)
    val browsingService = BrowsingService(wikiRepository, initializationScope)
    override val screenState: Flow<PresentationState> = browsingService.presentationState
    private val keyStrokeToIntentUseCase = KeyStrokeToIntentUseCase(terminalScreen.keyFlow, browsingService, initializationScope)

    init {
        keyStrokeToIntentUseCase.start()
    }


}