package com.alaeri.log.core.basic

import com.alaeri.log.core.LogEnvironment
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.context.LogContext

/**
 * Created by Emmanuel Requier on 16/12/2020.
 */
class BasicLogEnvironment(
    override val context: LogContext,
    override val collector: LogCollector): LogEnvironment(){

    override fun prepare() {}

    override fun dispose() {}

}