package com.alaeri.log.core.collector

import com.alaeri.log.core.Log

/**
 * LogCollector allows you to collect logging metadata and state and do what you want with it
 *
 * @see NoopCollector - use this one when creating a library at the boundaries
 * @see LogPrinter - use this one to get info in the console
 * @see LogBridge - use this one to get log data as a flow... <3
 * @see AndroidLogPrinter ....
 *
 * -------------------
 *
 * It implement the "+" operator so you can combine several as: LogPrinter() + LogPersister() + LogBridge()
 */
interface LogCollector {

    fun emit(log: Log)

    infix operator fun plus(other: LogCollector?): LogCollector {
        return if(other != null){
            LogCollectorsSet(setOf(this, other))
        }else{
            this
        }

    }
}