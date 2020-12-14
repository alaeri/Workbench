package com.alaeri.log.core.collector

import com.alaeri.log.core.LogState
import com.alaeri.log.core.context.LogContext

interface LogCollector {

    fun emit(logContext: LogContext, logState: LogState)

    infix operator fun plus(other: LogCollector?): LogCollector {
        return if(other != null){
            LogCollectorsSet(setOf(this, other))
        }else{
            this
        }

    }
}