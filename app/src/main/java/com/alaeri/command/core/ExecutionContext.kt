package com.alaeri.command.core

import com.alaeri.command.CommandState
import com.alaeri.command.Starting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ExecutionContext<R>: Invoker<R> {
    override val owner: Any
    fun emit(commandState: CommandState<R>)
    fun execute(executable: suspend ExecutionContext<R>.()->R): Flow<CommandState<R>> =
        flow {
            emit(Starting())
            val result = executable.invoke(this@ExecutionContext)
            emit(CommandState.Done(result))
        }
}