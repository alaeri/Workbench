package com.alaeri.presentation.wiki

import com.alaeri.domain.wiki.WikiRepository
import com.alaeri.presentation.InputState
import com.alaeri.presentation.PresentationState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Created by Emmanuel Requier on 30/11/2020.
 */
class BrowsingService(
    wikiRepository: WikiRepository,
    sharedCoroutineScope: CoroutineScope){

    private val shouldExitMutableStateFlow = MutableStateFlow(false)

    private val innerScope = sharedCoroutineScope.plus(SupervisorJob())

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


    val presentationState = shouldExitMutableStateFlow.flatMapLatest {
        if(it){
            println("exit.....")
            flowOf(PresentationState.Exit(listOf()))
        }else{
            combine(loadWikiOnPathUseCase.loadingStatusFlow, selectionRepository.selectionFlow, queryRepository.queryFlow, pathRepository.pathFlow){
                loadingStatus, internalLink, query, path ->
                println("combine.....")
                PresentationState.Presentation(InputState(query, path), loadingStatus, internalLink)
            }
        }
    }.shareIn(innerScope, SharingStarted.Lazily)

}