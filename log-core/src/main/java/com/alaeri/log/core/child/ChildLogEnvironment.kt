package com.alaeri.log.core.child

import com.alaeri.log.core.LogEnvironment
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.context.LogContext

class ChildLogEnvironment(override val context: LogContext, override val collector: LogCollector, private val dispose: ()->Unit): LogEnvironment() {
    override fun disposeBlocking() {
        dispose.invoke()
    }
}