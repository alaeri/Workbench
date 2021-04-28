package com.alaeri.presentation.wiki

import com.alaeri.domain.wiki.WikiRepository
import com.alaeri.log
import com.alaeri.logBlocking
import com.alaeri.logBlockingFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.plus

/**
 * Created by Emmanuel Requier on 30/11/2020.
 */
class BrowsingService(
    wikiRepository: WikiRepository,
    sharedCoroutineScope: CoroutineScope) {

    private val innerScope = sharedCoroutineScope.plus(SupervisorJob())

    private val shouldExitMutableStateFlow = MutableStateFlow(false)
    private val pathRepository = PathRepository()
    val queryRepository = QueryRepository()
    private val selectionRepository = SelectionRepository()

    private val loadWikiOnPathUseCase = LoadWikiOnPathUseCase(pathRepository, innerScope, wikiRepository)
    private val selectablesUseCase =  SelectablesUseCase(loadWikiOnPathUseCase, innerScope)
    private val resetSelectionOnNewNavigationUseCase = ResetSelectionOnNewNavigationUseCase(
        innerScope,
        selectablesUseCase,
        selectionRepository)
    private val selectUseCase = SelectUseCase(selectablesUseCase, selectionRepository)
    private val navigateToQueryUseCase = NavigateToQueryUseCase(queryRepository, pathRepository)
    private val navigateToSelectionUseCase = NavigateToSelectionUseCase(selectionRepository, pathRepository)
    private val onSelectionFetchPreviewUC = OnSelectionFetchPreviewUC(selectionRepository, wikiRepository)
    private val presentationUsecase = PresentationUsecase(innerScope,
        shouldExitMutableStateFlow,
        loadWikiOnPathUseCase,
        selectionRepository,
        queryRepository,
        pathRepository,
        onSelectionFetchPreviewUC
    )
    private val editUseCase = EditUseCase(queryRepository)
    private val clearSelectionUseCase = object  {
        fun execute(){
            selectionRepository.select(null)
        }
    }
    private val tabSelectionUseCase = object {
        var selectedTabIndex : Int = 0

    }


    suspend fun processIntent(intent: Intent) : Unit = logBlocking(name = "process intent") {
        when(intent){
            is Intent.Edit -> editUseCase.edit(intent)
            is Intent.Exit -> log(name = "exit") {
                    innerScope.cancel()
                    shouldExitMutableStateFlow.value = true
                }
            is Intent.NavigateToQuery -> navigateToQueryUseCase.navigateToCurrentQuery(intent)
            is Intent.SelectNextLink -> selectUseCase.selectNextLink(intent)
            is Intent.NavigateToSelection -> navigateToSelectionUseCase.navigateToCurrentSelection(intent)
            is Intent.ClearSelection -> clearSelectionUseCase.execute()
            is Intent.ChangeSelectedTab -> {}
        }
    }
    val presentationState = logBlockingFlow(name = "presentation state flow"){
        presentationUsecase.presentationStateFlow.shareIn(
            innerScope,
            SharingStarted.Lazily,
            1)
    }
}