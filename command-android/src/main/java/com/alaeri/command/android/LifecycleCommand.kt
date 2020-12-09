package com.alaeri.command.android

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.alaeri.command.CommandNomenclature
import com.alaeri.command.ICommandLogger
import com.alaeri.command.core.CommandScope
import com.alaeri.command.core.invokeCommand
import com.alaeri.command.core.root.ICommandScopeOwner
import com.alaeri.command.core.root.buildRootCommandScope
import com.alaeri.command.core.suspend.SuspendingCommandScope
import com.alaeri.command.core.suspend.suspendInvokeCommand
import com.alaeri.command.di.DelayedCommandLogger
import kotlinx.coroutines.flow.Flow

interface LifecycleCommandOwner: LifecycleOwner, ICommandScopeOwner {

    fun buildLifecycleCommandRoot(delayedLogger: Flow<ICommandLogger?>) =
        buildRootCommandScope(
            this,
            name = "lifecycle",
            nomenclature = CommandNomenclature.Android.Lifecycle.Root,
            iCommandLogger = DelayedCommandLogger(lifecycleScope, delayedLogger)
        )

    val lifecycleCommandContext: LifecycleCommandContext

}

inline fun <reified R> LifecycleCommandOwner.invokeCommandWithLifecycle(
    noinline body: CommandScope<R>.()->R): R{
    return lifecycleCommandContext.currentExecutionContext!!.invokeCommand<Unit,R> {
        this.body()
    }
}
suspend inline fun <reified R> LifecycleCommandOwner.invokeSuspendingCommandWithLifecycle(
    noinline body: suspend SuspendingCommandScope<R>.()->R): R{
    return lifecycleCommandContext.currentExecutionContext!!.suspendInvokeCommand {
        this.body()
    }
}