package com.alaeri.presentation.wiki

import com.alaeri.command.CommandState
import com.alaeri.command.core.command
import com.alaeri.command.core.Command
import com.alaeri.command.core.invoke
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.suspendingCommand
import com.alaeri.command.core.suspendInvoke
import kotlinx.coroutines.flow.firstOrNull

class NavigateToQueryUseCase(private val queryRepository: QueryRepository,
                             private val pathRepository: PathRepository
){
   suspend fun navigateToCurrentQuery(intent: Intent.NavigateToQuery): SuspendingCommand<Unit> = suspendingCommand{
       emit(CommandState.Update(intent))
       val query = queryRepository.queryFlow.firstOrNull()
        invoke{ pathRepository.select(query) }
        suspendInvoke{ queryRepository.updateQuery("") }
   }
}