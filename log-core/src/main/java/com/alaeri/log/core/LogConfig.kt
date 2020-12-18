package com.alaeri.log.core

import com.alaeri.log.core.basic.BasicEnvironmentFactory
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.context.EmptyLogContext
import com.alaeri.log.core.context.LogContext

/**
 * Created by Emmanuel Requier on 18/12/2020.
 */
object LogConfig{

    var logEnvironmentFactory : LogEnvironmentFactory = BasicEnvironmentFactory

    suspend inline fun <reified T> log(logContext: LogContext = EmptyLogContext(),
                                       collector: LogCollector? = null,
                                       vararg params: Any? = arrayOf(),
                                       crossinline body :suspend ()->T) : T {
        return logEnvironmentFactory.log(logContext, collector, *params){
            body.invoke()
        }
    }

    inline fun <reified T> logBlocking(logContext: LogContext = EmptyLogContext(),
                                       collector: LogCollector? = null,
                                       vararg params: Any? = arrayOf(),
                                       body :()->T): T {
        return logEnvironmentFactory.logBlocking(logContext, collector, *params){
            body.invoke()
        }
    }
}