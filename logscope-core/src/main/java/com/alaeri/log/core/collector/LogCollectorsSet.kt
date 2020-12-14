package com.alaeri.log.core.collector

import com.alaeri.log.core.context.LogContext
import com.alaeri.log.core.LogState

data class LogCollectorsSet(val collectors: Set<LogCollector>): LogCollector {

    override fun emit(logContext: LogContext, logState: LogState) {
        collectors.forEach {
            it.emit(logContext, logState)
        }
    }

    override fun plus(other: LogCollector?): LogCollector {
        return when(other){
            is LogCollectorsSet -> LogCollectorsSet(collectors + other.collectors)
            is LogCollector -> LogCollectorsSet(collectors + other)
            else -> this
        }
    }
}