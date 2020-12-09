package com.alaeri.presentation.wiki

import com.alaeri.command.CommandState
import com.alaeri.command.ICommandLogger
import com.alaeri.command.core.flow.flowCommand
import com.alaeri.command.core.flow.shared
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.suspendingCommand
import com.alaeri.command.core.suspendInvoke
import com.alaeri.domain.wiki.WikiRepository
import com.alaeri.presentation.PresentationState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Created by Emmanuel Requier on 30/11/2020.
 */
class BrowsingService(
    wikiRepository: WikiRepository,
    sharedCoroutineScope: CoroutineScope,
    iCommandLogger: ICommandLogger) {

    private val innerScope = sharedCoroutineScope.plus(SupervisorJob())

    private val shouldExitMutableStateFlow = MutableStateFlow(false)
    private val pathRepository = PathRepository()
    private val queryRepository = QueryRepository()
    private val selectionRepository = SelectionRepository()

    private val loadWikiOnPathUseCase = LoadWikiOnPathUseCase(pathRepository, innerScope, wikiRepository)
    private val selectablesUseCase =  SelectablesUseCase(loadWikiOnPathUseCase, innerScope, iCommandLogger)
    private val resetSelectionOnNewNavigationUseCase = ResetSelectionOnNewNavigationUseCase(
        innerScope,
        selectablesUseCase,
        selectionRepository,
        iCommandLogger)
    private val selectUseCase = SelectUseCase(selectablesUseCase, selectionRepository)
    private val navigateToQueryUseCase = NavigateToQueryUseCase(queryRepository, pathRepository)
    private val navigateToSelectionUseCase = NavigateToSelectionUseCase(selectionRepository, pathRepository)
    private val presentationUsecase = PresentationUsecase(innerScope,
        shouldExitMutableStateFlow,
        loadWikiOnPathUseCase,
        selectionRepository,
        queryRepository,
        pathRepository
    )
    private val editUseCase = EditUseCase(queryRepository)

    suspend fun processIntent(intent: Intent) : SuspendingCommand<Unit> = suspendingCommand(name = "process intent") {
        emit(CommandState.Update(intent))
        val comm = when(intent){
            is Intent.Edit -> suspendInvoke { editUseCase.edit(intent) }
            is Intent.Exit -> suspendInvoke {
                suspendingCommand<Unit>(name = "exit") {
                    innerScope.cancel()
                    shouldExitMutableStateFlow.value = true
                }
            }
            is Intent.NavigateToQuery -> suspendInvoke { navigateToQueryUseCase.navigateToCurrentQuery(intent) }
            is Intent.SelectNextLink -> suspendInvoke { selectUseCase.selectNextLink(intent) }
            is Intent.NavigateToSelection -> suspendInvoke { navigateToSelectionUseCase.navigateToCurrentSelection(intent) }
        }
    }
    val presentationStateCommand =
        flowCommand<PresentationState>(name = "presentation state flow") {
            syncInvokeFlow { presentationUsecase.presentationStateInCommand }.shareIn(
                innerScope,
                SharingStarted.Lazily,
                1
            )
        }.shared()
    //val presentationStateCommand = flowCommand<PresentationState> { syncInvokeFlow { presentationUsecase.presentationStateInCommand } }

}