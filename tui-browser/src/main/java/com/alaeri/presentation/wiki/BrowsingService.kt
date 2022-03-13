package com.alaeri.presentation.wiki

import com.alaeri.domain.wiki.WikiArticle
import com.alaeri.domain.wiki.WikiRepository
import com.alaeri.log
import com.alaeri.presentation.PresentationState
import com.alaeri.presentation.tui.wrap.LineWrapper
import com.googlecode.lanterna.TerminalSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus

/**
 * Created by Emmanuel Requier on 30/11/2020.
 */
data class PanelSizes(val left: TerminalSize, val right: TerminalSize)
class BrowsingService(
    wikiRepository: WikiRepository,
    sharedCoroutineScope: CoroutineScope,
    combineScope: CoroutineScope,
    val sizesFlow: Flow<PanelSizes>
) {

    private val innerScope = sharedCoroutineScope.plus(SupervisorJob())

    private val shouldExitMutableStateFlow = MutableStateFlow(false)
    private val pathRepository = PathRepository()
    val queryRepository = QueryRepository()
    private val selectionRepository = SelectionRepository()

    val cache = mutableMapOf<String, WikiArticle>()

    private val loadWikiOnPathUseCase = LoadWikiOnPathUseCase(pathRepository, innerScope, wikiRepository, cache)
    private val selectablesUseCase =  SelectablesUseCase(loadWikiOnPathUseCase, innerScope)
    private val resetSelectionOnNewNavigationUseCase = ResetSelectionOnNewNavigationUseCase(
        innerScope,
        selectablesUseCase,
        selectionRepository)
    private val selectUseCase = SelectUseCase(selectablesUseCase, selectionRepository)
    private val navigateToQueryUseCase = NavigateToQueryUseCase(queryRepository, pathRepository)
    private val navigateToSelectionUseCase = NavigateToSelectionUseCase(selectionRepository, pathRepository)
    private val onSelectionFetchPreviewUC = OnSelectionFetchPreviewUC(selectionRepository, wikiRepository, cache)
    private val reflowLeftPanelUC = ReflowUC(sizeFlow = sizesFlow.map { it.left }, loadWikiOnPathUseCase.loadingStatus, LineWrapper())
    private val reflowRightPanelUC = ReflowUC(sizeFlow = sizesFlow.map { it.right }, onSelectionFetchPreviewUC.selectionPreview, LineWrapper())
    private val presentationUsecase = PresentationUsecase(combineScope,
        shouldExitMutableStateFlow,
        reflowLeftPanelUC,
        selectionRepository,
        queryRepository,
        pathRepository,
        reflowRightPanelUC
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