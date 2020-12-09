package com.alaeri.command.core

import com.alaeri.command.CommandState
import com.alaeri.command.Starting
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.supervisorScope

interface CommandScope<R>: Invoker<R> {
    override val owner: Any
    fun emit(commandState: CommandState<R>)

    fun execute(executable: suspend CommandScope<R>.()->R): Flow<CommandState<R>> =
        flow {
            supervisorScope {
                emit(Starting())
                try {
                    val result = coroutineScope { this@CommandScope.executable() }
                    emit(CommandState.Done(result))
                }catch (e: Exception){
                    emit(CommandState.Failure<R>(e))
                }
            }
        }
}