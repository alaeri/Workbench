package com.alaeri.command.android

import androidx.lifecycle.LifecycleOwner
import com.alaeri.command.CommandState
import com.alaeri.command.core.*
import com.alaeri.command.core.suspend.SuspendingExecutionContext
import com.alaeri.command.core.suspend.suspendInvokeCommand
import com.alaeri.command.invokeSuspendingCommand
import com.alaeri.command.invokeSyncCommand

class LifecycleCommandContext(
    val owner: LifecycleOwner,
    private val commandLogger: ICommandLogger<Any>
): IInvokationContext<Any, Any> {

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
        override val nomenclature: CommandNomenclature
            get() = CommandNomenclature.Undefined
        override val name: String?
            get() = "lifecycle?"
    }
    override val invoker: Invoker<Any> = object : Invoker<Any> {
        override val owner =  this@LifecycleCommandContext.owner
    }

    override fun emit(opState: CommandState<Any>) {
        commandLogger.log(opState)
    }
}