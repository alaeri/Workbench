package com.alaeri.presentation.tui

import com.alaeri.command.core.suspend.SuspendingCommand
import kotlinx.coroutines.CoroutineScope

interface IViewModelFactory {
    suspend fun provideViewModel(terminalScreen: ITerminalScreen, initializationScope: CoroutineScope): SuspendingCommand<ITerminalViewModel>
}