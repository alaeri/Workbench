package com.alaeri.presentation.wiki

import com.alaeri.command.DefaultIRootCommandLogger
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.suspendingCommand
import com.alaeri.presentation.tui.ITerminalScreen
import com.alaeri.presentation.tui.ITerminalViewModel
import com.alaeri.presentation.tui.IViewModelFactory
import com.alaeri.domain.ILogger
import com.alaeri.domain.wiki.WikiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ViewModelFactory(private val wikiRepository: WikiRepository,
                       private val logger: ILogger,
                       private val commandLogger: DefaultIRootCommandLogger
): IViewModelFactory {

    override suspend fun provideViewModel(
        terminalScreen: ITerminalScreen,
        initializationScope: CoroutineScope,
    ): SuspendingCommand<ITerminalViewModel>  = suspendingCommand{
        TerminalViewModel(initializationScope, terminalScreen, wikiRepository, logger, commandLogger)
    }

}