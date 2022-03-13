package com.alaeri.presentation.wiki

import com.alaeri.domain.ILogger
import com.alaeri.domain.wiki.WikiRepository
import com.alaeri.log
import com.alaeri.presentation.tui.ITerminalScreen
import com.alaeri.presentation.tui.ITerminalViewModel
import com.alaeri.presentation.tui.IViewModelFactory
import kotlinx.coroutines.*
import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
class ViewModelFactory(private val wikiRepository: WikiRepository,
                       private val logger: ILogger? = null
): IViewModelFactory {

    override suspend fun provideViewModel(
        terminalScreen: ITerminalScreen,
        initializationScope: CoroutineScope,
    ): ITerminalViewModel  = log("provide view model"){
        val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        log("provideViewModel"){
            val scope2 = initializationScope + dispatcher
            TerminalViewModel(initializationScope, terminalScreen, wikiRepository, scope2).also {
                println("ok2")
            }

        }
    }

}