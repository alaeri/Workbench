package com.alaeri.presentation.wiki

import com.alaeri.command.core.flow.flowCommand
import com.alaeri.command.core.flow.shared
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class LoadWikiOnPathUseCase(private val pathRepository: PathRepository,
                            private val sharedCoroutineScope: CoroutineScope,
                            private val wikiRepository: WikiRepository
){

    val loadingStatusInCommand = flowCommand<LoadingStatus>(name = "loading status flow") {
        val pathFlow: Flow<String?> = syncInvokeFlow { pathRepository.pathFlowCommand }.distinctUntilChanged()
        pathFlow.flatMapLatest { path ->
            if(path.isNullOrBlank()){
                flowOf(LoadingStatus.Loading("----"))
            }else{
               syncInvokeFlow { wikiRepository.loadWikiArticleCommand(path) }
            }
        }.shareIn(sharedCoroutineScope, replay = 1, started = SharingStarted.Lazily)
    }.shared()
}
