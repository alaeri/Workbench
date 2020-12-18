package com.alaeri.log.core

import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.child.ChildLogEnvironmentFactory
import com.alaeri.log.core.context.EmptyLogContext
import com.alaeri.log.core.context.LogContext



suspend inline fun <reified T> log(logContext: LogContext = EmptyLogContext(),
                                   collector: LogCollector? = null,
                                   vararg params: Any? = arrayOf(),
                                   crossinline body :suspend ()->T) : T  =
    LogConfig.log(logContext, collector, *params){
        body.invoke()
    }

inline fun <reified T> logBlocking(logContext: LogContext = EmptyLogContext(),
                                   collector: LogCollector? = null,
                                   vararg params: Any? = arrayOf(),
                                   body :()->T): T =
    LogConfig.logBlocking(logContext, collector, *params) {
        body.invoke()
    }


