package com.alaeri.presentation.wiki

import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiText
import com.alaeri.logBlocking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class SelectablesUseCase(
    private val loadWikiOnPathUseCase: LoadWikiOnPathUseCase,
    private val sharedCoroutineScope: CoroutineScope
){


    val selectablesFlow : Flow<List<WikiText.InternalLink>> = logBlocking(name = "selectables flow") {
        val loadingStatusFlow = loadWikiOnPathUseCase.loadingStatus
        loadingStatusFlow.map {
                when(it){
                    is LoadingStatus.Done -> it.result.lines.flatMap { it.mapNotNull { it as? WikiText.InternalLink } }
                    else -> listOf<WikiText.InternalLink>()
                }
            }.distinctUntilChanged().shareIn(sharedCoroutineScope, replay = 1, started = SharingStarted.Lazily)
    }
}