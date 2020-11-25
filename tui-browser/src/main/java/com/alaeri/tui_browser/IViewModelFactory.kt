package com.alaeri.tui_browser

import kotlinx.coroutines.CoroutineScope

interface IViewModelFactory {
    suspend fun provideViewModel(terminalScreen: ITerminalScreen, initializationScope: CoroutineScope): ITerminalViewModel
}