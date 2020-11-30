package com.alaeri.presentation.wiki

import com.alaeri.command.CommandState
import com.alaeri.command.core.command
import com.alaeri.command.core.Command
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.suspendingCommand
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

class QueryRepository{
    private val mutableQuery = MutableStateFlow<String>("")
    suspend fun updateQuery(newQuery: String) : SuspendingCommand<Unit> = suspendingCommand{
        emit(CommandState.Update(newQuery))
        mutableQuery.value = newQuery
    }
    val queryFlow: SharedFlow<String> = mutableQuery
}