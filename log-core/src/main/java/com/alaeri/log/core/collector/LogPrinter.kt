package com.alaeri.log.core.collector

import com.alaeri.log.core.LogState
import com.alaeri.log.core.context.LogContext

class LogPrinter: LogCollector {
    override fun emit(logContext: LogContext, logState: LogState) {
        println("logContext: $logContext -> logState: $logState")
    }
}