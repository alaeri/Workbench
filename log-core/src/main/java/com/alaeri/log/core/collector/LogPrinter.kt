package com.alaeri.log.core.collector

import com.alaeri.log.core.Log

class LogPrinter: LogCollector {
    override fun emit(log: Log) {
        println("tag: ${log.tag} -> message: ${log.message}")
    }
}