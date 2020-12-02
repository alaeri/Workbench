package com.alaeri.presentation.wiki

import com.alaeri.command.CommandState
import com.alaeri.command.core.suspend.suspendingCommand
import com.alaeri.command.core.suspendInvoke

class EditUseCase(private val queryRepository: QueryRepository){

    suspend fun edit(intent: Intent.Edit) = suspendingCommand<Unit> {
        emit(CommandState.Update(intent))
        suspendInvoke {
            queryRepository.updateQuery(intent.newQuery)
        }

    }
}