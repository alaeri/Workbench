package com.alaeri.tui_browser

import com.alaeri.tui_browser.wiki.WikiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ViewModelFactory(private val wikiRepository: WikiRepository,
                       private val logger: ILogger): IViewModelFactory {

    override suspend fun provideViewModel(
        terminalScreen: ITerminalScreen,
        initializationScope: CoroutineScope,
    ): ITerminalViewModel {
        return TerminalViewModel(initializationScope, terminalScreen, wikiRepository, logger)
    }

}