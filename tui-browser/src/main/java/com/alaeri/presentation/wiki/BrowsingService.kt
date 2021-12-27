package com.alaeri.presentation.wiki

import com.alaeri.domain.wiki.WikiRepository
import com.alaeri.log
import com.alaeri.logBlocking
import com.alaeri.logBlockingFlow
import com.alaeri.presentation.InputState
import com.alaeri.presentation.PresentationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus

/**
 * Created by Emmanuel Requier on 30/11/2020.
 */
class BrowsingService(
    wikiRepository: WikiRepository,
    sharedCoroutineScope: CoroutineScope,
    combineScope: CoroutineScope
) {

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
    private val presentationUsecase = PresentationUsecase(combineScope,
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


    suspend fun processIntent(intent: Intent) : Unit = log(name = "process intent") {
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
    val presentationState : Flow<PresentationState> = presentationUsecase.presentationStateFlow.stateIn(
            combineScope,
            SharingStarted.Lazily,
            PresentationState.Loading).log(name = "presentation state flow")

}