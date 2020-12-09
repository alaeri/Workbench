package com.alaeri.command.core.flow

import com.alaeri.command.CommandNomenclature
import com.alaeri.command.CommandState
import com.alaeri.command.Starting
import com.alaeri.command.Value
import com.alaeri.command.core.ICommand
import com.alaeri.command.core.ChainableCommandScope
import com.alaeri.command.core.suspend.SuspendingCommandScope
import kotlinx.coroutines.flow.*

interface IFlowCommand<R>: ICommand<R>{
    val chainableCommandScope: ChainableCommandScope<R>
    fun execute(syncOrSuspendExecutionContext: SuspendingCommandScope<R>): Flow<CommandState<R>>
}

data class FlowCommand<R>(override val owner: Any,
                          override val nomenclature: CommandNomenclature,
                          override val name: String?,
                          override val chainableCommandScope: ChainableCommandScope<R>,
                          val executable: SuspendingCommandScope<R>.()-> Flow<R>
): IFlowCommand<R> {
    override fun execute(syncOrSuspendExecutionContext: SuspendingCommandScope<R>): Flow<CommandState<R>> = executable.invoke(syncOrSuspendExecutionContext)
        .map<R, CommandState<R>> {
            Value(
                it
            )
        }
        .onStart { emit(Starting()) }
        .onCompletion {  }
        .onEach { syncOrSuspendExecutionContext.emit(it) }
}