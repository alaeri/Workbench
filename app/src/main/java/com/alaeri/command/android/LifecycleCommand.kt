package com.alaeri.command.android

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.alaeri.command.CommandState
import com.alaeri.command.core.*
import com.alaeri.command.core.suspend.SuspendingExecutionContext
import com.alaeri.command.core.suspend.suspendInvokeCommand
import com.alaeri.command.invokeSuspendingCommand
import com.alaeri.command.invokeSyncCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * We want to build an invokation context that will receive a logger property after initialization
 *
 * It needs to store the commandsStates received before the logger property is initialized
 * And send these events and all subsequent ones once it is initialized
 *
 */

interface CommandLogger<R>{
    fun log(state: CommandState<R>)
}
interface DelayedLogOwner<R>{
    val futureLogger : Flow<CommandLogger<R>?>
}
class DelayedCommandLogger<R>(val scope: CoroutineScope, val delayedLogOwner: DelayedLogOwner<R>): CommandLogger<R>{

    private val initialLogs = mutableListOf<CommandState<R>>()
    var commandLogger: CommandLogger<R>? = null

    init {
        scope.launch {
            waitForFutureLogger()
        }
    }

    suspend fun waitForFutureLogger(){
        val logger = delayedLogOwner.futureLogger.filterNotNull().first()
        initialLogs.forEach { logger.log(it) }
        initialLogs.clear()
        commandLogger = logger
    }

    override  fun log(state: CommandState<R>) {
        val logger = commandLogger
        if(logger != null){
            logger.log(state)
        }else{
            initialLogs.add(state)
        }
    }
}
interface DelayedLogLifecycleCommandOwner: LifecycleOwner, DelayedLogOwner<Any>{
    val commandContext : LifecycleCommandContext
    fun buildLifecycleCommandContext() = LifecycleCommandContext(this, DelayedCommandLogger<Any>(lifecycleScope, this))
    fun <R> invokeLifecycleCommand(body: ExecutionContext<Any>.()->R) = commandContext.invokeLifecycleCommand(body)
    suspend fun <R> invokeSuspendingLifecycleCommand(body: suspend SuspendingExecutionContext<Any>.()->R) = commandContext.invokeSuspendingLifecycleCommand(body)
}


class LifecycleCommandContext(
    val owner: LifecycleOwner,
    private val commandLogger: CommandLogger<Any>): IInvokationContext<Any, Any>{

    fun <R> invokeLifecycleCommand(body: ExecutionContext<Any>.()->R){
        invokeSyncCommand<Any>(this){
             this.invokeCommand { body }
        }
    }
    suspend inline fun <R> invokeSuspendingLifecycleCommand(noinline body: suspend SuspendingExecutionContext<Any>.()->R){
        invokeSuspendingCommand<Any>(this){
            this.suspendInvokeCommand { body }
        }
    }

    override val command: ICommand<Any> = object : ICommand<Any> {
        override val owner: Any = this@LifecycleCommandContext.owner
    }
    override val invoker: Invoker<Any> = object : Invoker<Any>{
        override val owner =  this@LifecycleCommandContext.owner
    }

    override fun emit(opState: CommandState<Any>) {
        commandLogger.log(opState)
    }
}


