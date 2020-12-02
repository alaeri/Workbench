package com.alaeri.presentation.wiki

import com.alaeri.command.core.flow.flowCommand
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class LoadWikiOnPathUseCase(private val pathRepository: PathRepository,
                            private val sharedCoroutineScope: CoroutineScope,
                            private val wikiRepository: WikiRepository
){

    val loadingStatusFlow : SharedFlow<LoadingStatus> = pathRepository
        .pathFlow.flatMapLatest { path ->
            if(path.isNullOrBlank()){
                flowOf(LoadingStatus.Loading("----"))
            }else{
                wikiRepository.loadWikiArticle(path)
            }
        }.shareIn(sharedCoroutineScope, replay = 1, started = SharingStarted.Lazily)

    val loadingStatusInCommand = flowCommand<LoadingStatus> {
        val pathFlow: Flow<String?> = syncInvokeFlow { pathRepository.pathFlowCommand }
        pathFlow.flatMapLatest { path ->
            if(path.isNullOrBlank()){
                flowOf(LoadingStatus.Loading("----"))
            }else{
                wikiRepository.loadWikiArticle(path)
            }
        }.shareIn(sharedCoroutineScope, replay = 1, started = SharingStarted.Lazily)
    }
}
