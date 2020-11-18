package com.alaeri.command.core.suspend

import com.alaeri.command.CommandState
import com.alaeri.command.Starting
import com.alaeri.command.core.ExecutionContext
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface SuspendingExecutionContext<R>: ExecutionContext<R> {
    override val owner: Any
    override fun emit(commandState: CommandState<R>)
    fun executeAsFlow(emitAndReturn: SuspendingExecutionContext<out R>. ()->R): Flow<CommandState<R>> =
        flow {
            emit(Starting<R>())
            try{
                val result = coroutineScope { println("csC: $this") ; this@SuspendingExecutionContext.emitAndReturn() }
                emit(CommandState.Done<R>(result))
            }catch (e: Throwable){
                emit(CommandState.Failure<R>(e))
            }
        }
}