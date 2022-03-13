package com.alaeri.presentation.wiki

import com.alaeri.domain.wiki.EmptyStatusReason
import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiArticle
import com.alaeri.domain.wiki.WikiRepository
import com.alaeri.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 * Created by Emmanuel Requier on 23/04/2021.
 */
class OnSelectionFetchPreviewUC(
    private val selectionRepository: SelectionRepository,
    private val wikiRepository: WikiRepository,
    private val cache: MutableMap<String, WikiArticle>
) {

    val selectionPreview: Flow<LoadingStatus> = selectionRepository.selectionFlow.flatMapLatest {
        selection ->
        if(selection == null){
            flowOf(LoadingStatus.Empty(EmptyStatusReason.NotInitialized))
        }else{
            val target = selection.content.target
            val result = cache[target]?.let {
                flowOf(LoadingStatus.Done(it))
            }?:  wikiRepository.loadWikiArticle(target).onEach {
                if(it is LoadingStatus.Done){
                    cache[target] = it.result
                }
            }
            result.log("load wiki internal")
        }
    }.log("load wiki ext").flowOn(Dispatchers.IO)
}