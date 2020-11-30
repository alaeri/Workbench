package com.alaeri.presentation.wiki

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
        }.shareIn(sharedCoroutineScope, SharingStarted.Lazily)
}