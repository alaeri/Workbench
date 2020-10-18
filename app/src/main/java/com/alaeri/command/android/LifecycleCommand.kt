package com.alaeri.command.android

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.alaeri.cats.app.DefaultIRootCommandLogger
import com.alaeri.command.di.DelayedCommandLogger
import kotlinx.coroutines.flow.Flow

interface LifecycleCommandOwner: LifecycleOwner {
    val commandContext : LifecycleCommandContext
    fun buildLifecycleCommandContext(delayedLogger: Flow<DefaultIRootCommandLogger?>) = LifecycleCommandContext(this, DelayedCommandLogger(lifecycleScope, delayedLogger))

}
