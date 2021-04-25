package com.alaeri.presentation.wiki

import com.alaeri.domain.wiki.WikiRepository
import com.alaeri.logBlocking
import com.alaeri.presentation.PresentationState
import com.alaeri.presentation.tui.ITerminalScreen
import com.alaeri.presentation.tui.ITerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TerminalViewModel(
    initializationScope: CoroutineScope,
    terminalScreen: ITerminalScreen,
    wikiRepository: WikiRepository,
): ITerminalViewModel {


    private val browsingService = BrowsingService(wikiRepository, initializationScope)
    override val screenState: Flow<PresentationState> = browsingService.presentationState
    private val keyStrokeToIntentUseCase = KeyStrokeToIntentUseCase(terminalScreen.keyFlow, browsingService, initializationScope)

    init {
        logBlocking("process key strokes"){
            keyStrokeToIntentUseCase.start()
        }
    }


}