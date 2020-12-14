package com.alaeri.log.core.collector

import com.alaeri.log.core.context.LogContext
import com.alaeri.log.core.LogState

/**
 * Created by Emmanuel Requier on 14/12/2020.
 */
object NoopCollector : LogCollector{
    override fun emit(logContext: LogContext, logState: LogState) {
        //Here goes nothing
    }
}