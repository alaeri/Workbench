package com.alaeri.presentation.wiki

import com.alaeri.command.*
import com.alaeri.command.core.flow.FlowCommand
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.command.core.invoke
import com.alaeri.domain.ILogger
import com.alaeri.domain.wiki.WikiRepository
import com.alaeri.presentation.PresentationState
import com.alaeri.presentation.tui.ITerminalScreen
import com.alaeri.presentation.tui.ITerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
class TerminalViewModel(
    private val initializationScope: CoroutineScope,
    private val terminalScreen: ITerminalScreen,
    private val wikiRepository: WikiRepository,
    private val logger: ILogger,
    private val commandLogger: DefaultIRootCommandLogger
): ITerminalViewModel, ICommandRootOwner {


    override val commandRoot = buildCommandRoot(this, "instantiation root", CommandNomenclature.Root, commandLogger)
    val browsingService = BrowsingService(wikiRepository, initializationScope, commandLogger)
    override val screenState: Flow<PresentationState> = invokeRootCommand("getPresentationState", CommandNomenclature.Application.Start){ syncInvokeFlow { browsingService.presentationStateCommand } }
    override val screenStateCommand: FlowCommand<PresentationState> = browsingService.presentationStateCommand
    private val keyStrokeToIntentUseCase = KeyStrokeToIntentUseCase(terminalScreen.keyFlow, browsingService, initializationScope)

    init {
        invokeRootCommand<Unit>("process key strokes", CommandNomenclature.Application.Start){
            invoke{
                keyStrokeToIntentUseCase.start()
            }
        }
    }


}