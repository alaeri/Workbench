package com.alaeri.command.core

import com.alaeri.command.CommandState
import com.alaeri.command.core.suspend.SuspendingExecutionContext
import kotlinx.coroutines.flow.*

data class Command<R>(override val owner: Any,
                      val executableContext: ExecutableContext<R>,
                      val executable: SuspendingExecutionContext<R>.()-> Flow<CommandState<R>>): ICommand<R> {
    fun syncExecute(syncOrSuspendExecutionContext: SuspendingExecutionContext<R>): Flow<CommandState<R>> = executable.invoke(syncOrSuspendExecutionContext).onEach { syncOrSuspendExecutionContext.emit(it) }
}
