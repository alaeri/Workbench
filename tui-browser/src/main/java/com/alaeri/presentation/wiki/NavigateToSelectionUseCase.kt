package com.alaeri.presentation.wiki

import com.alaeri.command.CommandState
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.invoke
import com.alaeri.command.core.suspend.suspendingCommand
import kotlinx.coroutines.flow.firstOrNull

class NavigateToSelectionUseCase(private val selectionRepository: SelectionRepository, private val pathRepository: PathRepository){

    suspend fun navigateToCurrentSelection(intent: Intent.NavigateToSelection): SuspendingCommand<Unit> = suspendingCommand {
        emit(CommandState.Update(intent))
        val selection = syncInvokeFlow { selectionRepository.selectionFlowCommand }.firstOrNull()
        if(selection != null){
            invoke {
                pathRepository.select(selection.target)
            }
        }
    }
}