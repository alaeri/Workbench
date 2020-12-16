package com.alaeri.log.core.child

import com.alaeri.log.core.LogEnvironment
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.context.LogContext

class ChildLogEnvironment(override val context: LogContext, override val collector: LogCollector,
                          private val prepare: ChildLogEnvironment.()->Unit,
                          private val dispose: ()->Unit): LogEnvironment() {

    override fun dispose() = dispose.invoke()
    override fun prepare() = prepare.invoke(this)
}