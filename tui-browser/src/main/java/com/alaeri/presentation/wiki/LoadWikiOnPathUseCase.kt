package com.alaeri.presentation.wiki

import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiRepository
import com.alaeri.logBlocking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class LoadWikiOnPathUseCase(private val pathRepository: PathRepository,
                            private val sharedCoroutineScope: CoroutineScope,
                            private val wikiRepository: WikiRepository
){

    val loadingStatus = logBlocking(name = "loading status flow") {
        val pathFlow: Flow<String?> = pathRepository.pathFlow.distinctUntilChanged()
        pathFlow.flatMapLatest { path ->
            if(path.isNullOrBlank()){
                flowOf(LoadingStatus.Loading("----"))
            }else{
                wikiRepository.loadWikiArticle(path)
            }
        }.shareIn(sharedCoroutineScope, replay = 1, started = SharingStarted.Lazily)
    }
}
