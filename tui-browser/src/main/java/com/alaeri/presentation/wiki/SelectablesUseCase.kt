package com.alaeri.presentation.wiki

import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class SelectablesUseCase(
    private val sharedLoadingStatusFlow: SharedFlow<LoadingStatus>,
    private val sharedCoroutineScope: CoroutineScope
){
    val selectablesFlow : SharedFlow<List<WikiText.InternalLink>> = sharedLoadingStatusFlow.map {
        when(it){
            is LoadingStatus.Done -> it.result.lines.flatMap { it.mapNotNull { it as? WikiText.InternalLink } }
            else -> listOf<WikiText.InternalLink>()
        }
    }.shareIn(sharedCoroutineScope, SharingStarted.Lazily)
}