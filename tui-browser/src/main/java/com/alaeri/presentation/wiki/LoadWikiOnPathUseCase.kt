package com.alaeri.presentation.wiki

import com.alaeri.domain.wiki.EmptyStatusReason
import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiArticle
import com.alaeri.domain.wiki.WikiRepository
import com.alaeri.log
import com.alaeri.logBlocking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class LoadWikiOnPathUseCase(private val pathRepository: PathRepository,
                            private val sharedCoroutineScope: CoroutineScope,
                            private val wikiRepository: WikiRepository,
                            private val cache: MutableMap<String, WikiArticle>
){

    val loadingStatus by lazy{
        val pathFlow: Flow<String?> = pathRepository.pathFlow.distinctUntilChanged()
        pathFlow.flatMapLatest { path ->
            if(path.isNullOrBlank()){
                flowOf(LoadingStatus.Empty(EmptyStatusReason.NotInitialized))
            }else{
                cache[path]?.let {
                    flowOf(LoadingStatus.Done(it))
                }?:  wikiRepository.loadWikiArticle(path).onEach {
                    if(it is LoadingStatus.Done){
                        cache[path] = it.result
                    }
                }


            }
        }
        .flowOn(Dispatchers.IO)
        .log("loadingStatusFlow")
        .shareIn(sharedCoroutineScope, replay = 1, started = SharingStarted.Lazily)
    }
}
