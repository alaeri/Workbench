package com.alaeri.command.android

import androidx.lifecycle.LifecycleOwner
import com.alaeri.cats.app.DefaultIRootCommandLogger
import com.alaeri.command.buildCommandContextA
import com.alaeri.command.core.ExecutionContext
import com.alaeri.command.core.invokeCommand
import com.alaeri.command.core.suspend.SuspendingExecutionContext
import com.alaeri.command.core.suspend.suspendInvokeCommand
import com.alaeri.command.invokeSuspendingCommand
import com.alaeri.command.invokeSyncCommand

class LifecycleCommandContext(
    val owner: LifecycleOwner,
    val commandLogger: DefaultIRootCommandLogger
){
    val rootCommandContext = buildCommandContextA<Any?>(owner, "lifecycle", CommandNomenclature.Root) { c ->
        commandLogger.log(this, c)
    }

    inline fun <reified R: Any?> invokeLifecycleCommand(name: String? = null,
                                                  nomenclature: CommandNomenclature = CommandNomenclature.Undefined,
                                                  noinline body: ExecutionContext<R>.()->R) : R{
        return owner.invokeSyncCommand(rootCommandContext){
            invokeCommand<Any?,R> {
                body()
            }
        } as R
    }
    suspend inline fun <reified R: Any?> invokeSuspendingLifecycleCommand(noinline body: suspend SuspendingExecutionContext<R>.()->R) : R{
        return owner.invokeSuspendingCommand(rootCommandContext){
            return@invokeSuspendingCommand suspendInvokeCommand<Any?,R> {
                this.body()
            }
        } as R
    }
}