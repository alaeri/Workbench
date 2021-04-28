package com.alaeri.presentation.wiki

import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiArticle
import com.alaeri.domain.wiki.WikiRepository
import com.alaeri.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

/**
 * Created by Emmanuel Requier on 23/04/2021.
 */
class OnSelectionFetchPreviewUC(
    private val selectionRepository: SelectionRepository,
    private val wikiRepository: WikiRepository
) {

    val selectionPreview: Flow<LoadingStatus> = selectionRepository.selectionFlow.flatMapLatest {
        selection ->
        if(selection == null){
            flowOf(LoadingStatus.Error("selectSomething to see it appear here", RuntimeException()))
        }else{
            wikiRepository.loadWikiArticle(selection.content.target).log("load wiki internal")
        }
    }.log("load wiki ext").flowOn(Dispatchers.IO)

}