package com.alaeri.log.core.collector

import com.alaeri.log.core.Log

data class LogCollectorsSet(val collectors: Set<LogCollector>): LogCollector {

    override fun emit(log: Log) {
        collectors.forEach {
            it.emit(log)
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