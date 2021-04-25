package com.alaeri.presentation.tui

import kotlinx.coroutines.CoroutineScope

interface IViewModelFactory {
    suspend fun provideViewModel(terminalScreen: ITerminalScreen, initializationScope: CoroutineScope): ITerminalViewModel
}