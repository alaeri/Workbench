package com.alaeri.presentation.wiki

import com.alaeri.command.core.flow.flowCommand
import com.alaeri.command.core.flow.shared
import com.alaeri.command.core.flow.syncInvokeFlow
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
//    val presentationState = exitFlow.flatMapLatest {
//        if(it){
//            println("exit.....")
//            flowOf(PresentationState.Exit(listOf()))
//        }else{
//            combine(
//                loadWikiOnPathUseCase.loadingStatusFlow,
//                selectionRepository.selectionFlow,
//                queryRepository.queryFlow,
//                pathRepository.pathFlow
//            ) { loadingStatus, internalLink, query, path ->
//                //println("combine.....")
//                PresentationState.Presentation(InputState(query, path), loadingStatus, internalLink)
//            }
//        }
//    }.shareIn(sharingScope, replay = 1, started = SharingStarted.Lazily).onSubscription {
//        //println("subscribed: $this")
//    }.onEach {  }

    val presentationStateInCommand by lazy {
        flowCommand<PresentationState>(name = "presentation state flow") {
            exitFlow.flatMapLatest {
                if (it) {
                    flowOf(PresentationState.Exit(listOf()))
                } else {
                    combine(
                        syncInvokeFlow { loadWikiOnPathUseCase.loadingStatusInCommand },
                        syncInvokeFlow { selectionRepository.selectionFlowCommand },
                        syncInvokeFlow { queryRepository.queryFlowCommand },
                        syncInvokeFlow { pathRepository.pathFlowCommand }
                    ) { loadingStatus, internalLink, query, path ->
                        //println("combine.....")
                        PresentationState.Presentation(
                            InputState(query, path),
                            loadingStatus,
                            internalLink
                        )
                    }
                }
            }.shareIn(sharingScope, replay = 1, started = SharingStarted.Lazily).onSubscription {
                //println("subscribed: $this")
            }.onEach { }
        }.shared()
    }

}