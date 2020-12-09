package com.alaeri.command.core.suspend

import com.alaeri.command.CommandState
import com.alaeri.command.Starting
import com.alaeri.command.core.CommandScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface SuspendingCommandScope<R>: CommandScope<R> {
    override val owner: Any
    override fun emit(commandState: CommandState<R>)
    fun executeAsFlow(emitAndReturn: SuspendingCommandScope<out R>. ()->R): Flow<CommandState<R>> =
        flow {
            emit(Starting<R>())
            try{
                val result = coroutineScope { this@SuspendingCommandScope.emitAndReturn() }
                emit(CommandState.Done<R>(result))
            }catch (e: Throwable){
                emit(CommandState.Failure<R>(e))
            }
        }
}