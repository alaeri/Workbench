package com.alaeri.command.android

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.alaeri.command.di.DelayedLogger
import com.alaeri.command.core.*
import com.alaeri.command.core.suspend.SuspendingExecutionContext
import com.alaeri.command.di.DelayedCommandLogger
import kotlinx.coroutines.flow.Flow

interface LifecycleCommandOwner: LifecycleOwner {
    val commandContext : LifecycleCommandContext
    fun buildLifecycleCommandContext(delayedLogger: Flow<ICommandLogger<Any>?>) = LifecycleCommandContext(this, DelayedCommandLogger<Any>(lifecycleScope, delayedLogger))

}
