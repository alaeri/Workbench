package com.alaeri.command.android

import androidx.lifecycle.LifecycleOwner
import com.alaeri.command.*
import com.alaeri.command.core.*
import com.alaeri.command.core.suspend.SuspendingExecutionContext
import com.alaeri.command.core.suspend.suspendInvokeCommand

class LifecycleCommandContext(
    val owner: LifecycleOwner,
    val commandLogger: ICommandLogger<Any>
): IInvokationContext<Any, Any> {

    //val rootLifecycleContext = buildCommandRoot<Any>(owner, "", CommandNomenclature.Root, commandLogger)
    val rootCommandContext = buildCommandContextA<Any>(owner, "lifecycle", CommandNomenclature.Root) { c ->
        commandLogger.log(c)
    }

    inline fun <reified R: Any> invokeLifecycleCommand(name: String? = null,
                                                  nomenclature: CommandNomenclature = CommandNomenclature.Undefined,
                                                  noinline body: ExecutionContext<R>.()->R) : R{
//        val executionContext : ExecutionContext<R> = object : ExecutionContext<R>{
//            override val owner: Any
//                get() = this@LifecycleCommandContext.owner
//
//            override fun emit(commandState: CommandState<R>) {
//                commandLogger.log(commandState as CommandState<Any>)
//            }
//        }
//        return executionContext.execute { executionContext.body() }.syncFold()

        return invokeSyncCommand(rootCommandContext){
            invokeCommand<Any,R>(name, nomenclature) {
                this.body()
            }
        } as R
    }
    suspend inline fun <reified R> invokeSuspendingLifecycleCommand(noinline body: suspend SuspendingExecutionContext<Any>.()->R){
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