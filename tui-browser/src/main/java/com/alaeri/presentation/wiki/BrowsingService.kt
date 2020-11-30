package com.alaeri.presentation.wiki

import com.alaeri.domain.wiki.WikiRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Created by Emmanuel Requier on 30/11/2020.
 */
class BrowsingService(
    wikiRepository: WikiRepository,
    sharedCoroutineScope: CoroutineScope){

    private val innerScope = sharedCoroutineScope.plus(SupervisorJob())
    private val shouldExitMutableStateFlow = MutableStateFlow(false)
    private val pathRepository = PathRepository()
    private val queryRepository = QueryRepository()
    private val selectionRepository = SelectionRepository()
    private val loadWikiOnPathUseCase = LoadWikiOnPathUseCase(pathRepository, innerScope, wikiRepository)
    private val selectablesUseCase =  SelectablesUseCase(loadWikiOnPathUseCase.loadingStatusFlow, innerScope)
    private val resetSelectionOnNewNavigationUseCase = ResetSelectionOnNewNavigationUseCase(
        innerScope,
        selectablesUseCase.selectablesFlow,
        selectionRepository)
    private val selectUseCase = SelectUseCase(selectablesUseCase.selectablesFlow, selectionRepository)
    private val navigateToQueryUseCase = NavigateToQueryUseCase(queryRepository, pathRepository)
    private val navigateToSelectionUseCase = NavigateToSelectionUseCase(selectionRepository, pathRepository)
    private val presentationUsecase = PresentationUsecase(innerScope,
        shouldExitMutableStateFlow,
        loadWikiOnPathUseCase,
        selectionRepository,
        queryRepository,
        pathRepository
    )


    suspend fun processIntent(intent: Intent){
        when(intent){
            is Intent.Edit -> queryRepository.updateQuery(intent.newQuery)
            is Intent.Exit -> {
                innerScope.cancel()
                shouldExitMutableStateFlow.value = true
            }
            is Intent.NavigateToQuery -> navigateToQueryUseCase.navigateToCurrentQuery(intent)
            is Intent.SelectNextLink -> selectUseCase.selectNextLink(intent)
            is Intent.NavigateToSelection -> navigateToSelectionUseCase.navigateToCurrentSelection(intent)
        }
    }

    val presentationState = presentationUsecase.presentationState

}