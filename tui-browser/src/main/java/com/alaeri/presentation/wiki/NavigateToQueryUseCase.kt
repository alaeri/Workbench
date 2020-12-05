package com.alaeri.presentation.wiki

import com.alaeri.command.CommandState
import com.alaeri.command.core.command
import com.alaeri.command.core.Command
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.command.core.invoke
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.suspendingCommand
import com.alaeri.command.core.suspend.syncInvokeAsFlow
import com.alaeri.command.core.suspendInvoke
import kotlinx.coroutines.flow.firstOrNull

class NavigateToQueryUseCase(private val queryRepository: QueryRepository,
                             private val pathRepository: PathRepository
){
   suspend fun navigateToCurrentQuery(intent: Intent.NavigateToQuery): SuspendingCommand<Unit> = suspendingCommand(name="navigate to query"){
       emit(CommandState.Update(intent))
       val query = syncInvokeFlow { queryRepository.queryFlowCommand }.firstOrNull()
        invoke{ pathRepository.select(query) }
        suspendInvoke{ queryRepository.updateQuery("") }
   }
}