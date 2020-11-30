package com.alaeri.presentation.wiki

import com.alaeri.command.CommandState
import com.alaeri.command.DefaultIRootCommandLogger
import com.alaeri.command.core.command
import com.alaeri.command.core.invoke
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.invoke
import com.alaeri.command.core.suspend.suspendingCommand
import com.alaeri.command.core.suspendInvoke
import com.alaeri.domain.wiki.WikiRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Created by Emmanuel Requier on 30/11/2020.
 */
class BrowsingService(
    wikiRepository: WikiRepository,
    sharedCoroutineScope: CoroutineScope,
    iRootCommandLogger: DefaultIRootCommandLogger){

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
        selectionRepository,
        iRootCommandLogger)
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


    suspend fun processIntent(intent: Intent) : SuspendingCommand<Unit> = suspendingCommand {
        emit(CommandState.Update(intent))
        val comm = when(intent){
            is Intent.Edit -> suspendInvoke { queryRepository.updateQuery(intent.newQuery) }
            is Intent.Exit -> suspendInvoke {
                suspendingCommand<Unit> {
                    innerScope.cancel()
                    shouldExitMutableStateFlow.value = true
                }
            }
            is Intent.NavigateToQuery -> suspendInvoke { navigateToQueryUseCase.navigateToCurrentQuery(intent) }
            is Intent.SelectNextLink -> suspendInvoke { selectUseCase.selectNextLink(intent) }
            is Intent.NavigateToSelection -> suspendInvoke { navigateToSelectionUseCase.navigateToCurrentSelection(intent) }
        }
    }
    val presentationState = presentationUsecase.presentationState

}