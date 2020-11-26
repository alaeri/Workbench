package com.alaeri.presentation.wiki

import com.alaeri.presentation.tui.ITerminalScreen
import com.alaeri.presentation.tui.ITerminalViewModel
import com.alaeri.presentation.tui.IViewModelFactory
import com.alaeri.domain.ILogger
import com.alaeri.domain.wiki.WikiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ViewModelFactory(private val wikiRepository: WikiRepository,
                       private val logger: ILogger
): IViewModelFactory {

    override suspend fun provideViewModel(
        terminalScreen: ITerminalScreen,
        initializationScope: CoroutineScope,
    ): ITerminalViewModel {
        return TerminalViewModel(initializationScope, terminalScreen, wikiRepository, logger)
    }

}