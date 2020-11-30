package com.alaeri.presentation.wiki

import com.alaeri.presentation.InputState
import com.alaeri.presentation.PresentationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class PresentationUsecase(val sharingScope: CoroutineScope,
                          val exitFlow: Flow<Boolean>,
                          val loadWikiOnPathUseCase: LoadWikiOnPathUseCase,
                          val selectionRepository: SelectionRepository,
                          val queryRepository: QueryRepository,
                          val pathRepository: PathRepository
){
    val presentationState = exitFlow.flatMapLatest {
        if(it){
            println("exit.....")
            flowOf(PresentationState.Exit(listOf()))
        }else{
            combine(
                loadWikiOnPathUseCase.loadingStatusFlow,
                selectionRepository.selectionFlow,
                queryRepository.queryFlow,
                pathRepository.pathFlow
            ) { loadingStatus, internalLink, query, path ->
                //println("combine.....")
                PresentationState.Presentation(InputState(query, path), loadingStatus, internalLink)
            }
        }
    }.shareIn(sharingScope, replay = 1, started = SharingStarted.Lazily).onSubscription {
        //println("subscribed: $this")
    }.onEach {  }

}