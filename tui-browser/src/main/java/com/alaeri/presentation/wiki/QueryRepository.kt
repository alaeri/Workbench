package com.alaeri.presentation.wiki

import com.alaeri.command.CommandState
import com.alaeri.command.core.flow.FlowCommand
import com.alaeri.command.core.flow.IFlowCommand
import com.alaeri.command.core.flow.flowCommand
import com.alaeri.command.core.flow.shared
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.suspendingCommand
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

class QueryRepository{
    private val mutableQuery = MutableStateFlow<String>("")
    suspend fun updateQuery(newQuery: String) : SuspendingCommand<Unit> = suspendingCommand(name = "edit query command"){
        emit(CommandState.Update(newQuery))
        mutableQuery.value = newQuery
    }
    val queryFlow: SharedFlow<String> = mutableQuery
    val queryFlowCommand: IFlowCommand<String> = flowCommand<String>(name = "query flow") { queryFlow }.shared()
}