package com.alaeri.command.core.suspend

import com.alaeri.command.CommandNomenclature
import com.alaeri.command.CommandState
import com.alaeri.command.core.ICommand
import com.alaeri.command.core.ChainableCommandScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class SuspendingCommand<R>(override val owner: Any,
                           override val nomenclature: CommandNomenclature,
                           override val name: String?,
                           val chainableCommandScope: ChainableCommandScope<R>,
                           val executable: suspend SuspendingCommandScope<R>.()-> Flow<CommandState<R>>
):
    ICommand<R> {
    suspend fun suspendExecute(executionContext: SuspendingCommandScope<R>): Flow<CommandState<R>> = executionContext.executable().onEach { executionContext.emit(it) }
}