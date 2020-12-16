package com.alaeri.log.core.step

import com.alaeri.log.core.LogEnvironment
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.context.LogContext

/**
 * Created by Emmanuel Requier on 15/12/2020.
 */
class StepLogEnvironment(override val context: LogContext,
                         override val collector: LogCollector,
                         private val threadToJoin: Thread): LogEnvironment() {

    override fun disposeBlocking() {
        threadToJoin.join()
    }
}