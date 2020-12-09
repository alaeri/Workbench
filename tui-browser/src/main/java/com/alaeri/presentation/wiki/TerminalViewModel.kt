package com.alaeri.presentation.wiki

import com.alaeri.command.*
import com.alaeri.command.core.flow.IFlowCommand
import com.alaeri.command.core.invoke
import com.alaeri.command.core.root.ICommandScopeOwner
import com.alaeri.command.core.root.buildRootCommandScope
import com.alaeri.command.core.root.invokeRootCommand
import com.alaeri.domain.wiki.WikiRepository
import com.alaeri.presentation.PresentationState
import com.alaeri.presentation.tui.ITerminalScreen
import com.alaeri.presentation.tui.ITerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TerminalViewModel(
    initializationScope: CoroutineScope,
    terminalScreen: ITerminalScreen,
    wikiRepository: WikiRepository,
    commandLogger: ICommandLogger
): ITerminalViewModel, ICommandScopeOwner {


    override val commandScope = buildRootCommandScope(this, "instantiation root", CommandNomenclature.Root, commandLogger)
    private val browsingService = BrowsingService(wikiRepository, initializationScope, commandLogger)
    override val screenStateCommand: IFlowCommand<PresentationState> = browsingService.presentationStateCommand
    private val keyStrokeToIntentUseCase = KeyStrokeToIntentUseCase(terminalScreen.keyFlow, browsingService, initializationScope)

    init {
        invokeRootCommand<Unit>("process key strokes", CommandNomenclature.Application.Start){
            invoke{
                keyStrokeToIntentUseCase.start()
            }
        }
    }


}