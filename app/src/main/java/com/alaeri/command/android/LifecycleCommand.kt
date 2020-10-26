package com.alaeri.command.android

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.alaeri.cats.app.DefaultIRootCommandLogger
import com.alaeri.command.ICommandRootOwner
import com.alaeri.command.buildCommandRoot
import com.alaeri.command.core.ExecutionContext
import com.alaeri.command.core.invokeCommand
import com.alaeri.command.core.suspend.SuspendingExecutionContext
import com.alaeri.command.core.suspend.suspendInvokeCommand
import com.alaeri.command.di.DelayedCommandLogger
import kotlinx.coroutines.flow.Flow

interface LifecycleCommandOwner: LifecycleOwner, ICommandRootOwner {

    fun buildLifecycleCommandRoot(delayedLogger: Flow<DefaultIRootCommandLogger?>) = buildCommandRoot(this,
        name = "lifecycle",
        nomenclature = CommandNomenclature.Android.Lifecycle.Root,
        iRootCommandLogger = DelayedCommandLogger(lifecycleScope, delayedLogger))

    val lifecycleCommandContext: LifecycleCommandContext

}

inline fun <reified R> LifecycleCommandOwner.invokeCommandWithLifecycle(
    noinline body: ExecutionContext<R>.()->R): R{
    return lifecycleCommandContext.currentExecutionContext!!.invokeCommand<Unit,R> {
        this.body()
    }
}
suspend inline fun <reified R> LifecycleCommandOwner.invokeSuspendingCommandWithLifecycle(
    noinline body: suspend SuspendingExecutionContext<R>.()->R): R{
    return lifecycleCommandContext.currentExecutionContext!!.suspendInvokeCommand {
        this.body()
    }
}