package com.alaeri.log.core.collector

import com.alaeri.log.core.Log

/**
 * Created by Emmanuel Requier on 14/12/2020.
 */
object NoopCollector : LogCollector{
    override fun emit(log: Log) {
        //Here goes nothing
    }
}