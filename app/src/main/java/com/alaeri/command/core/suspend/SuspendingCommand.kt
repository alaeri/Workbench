package com.alaeri.command.core.suspend

import com.alaeri.command.CommandState
import com.alaeri.command.android.CommandNomenclature
import com.alaeri.command.core.ICommand
import com.alaeri.command.core.ExecutableContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class SuspendingCommand<R>(override val owner: Any,
                           override val nomenclature: CommandNomenclature,
                           override val name: String?,
                           val executableContext: ExecutableContext<R>,
                           val executable: suspend SuspendingExecutionContext<R>.()-> Flow<CommandState<R>>
):
    ICommand<R> {
    suspend fun suspendExecute(executionContext: SuspendingExecutionContext<R>): Flow<CommandState<R>> = executionContext.executable().onEach { executionContext.emit(it) }
}