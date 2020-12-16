package com.alaeri.log.core

import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.context.EmptyLogContext
import com.alaeri.log.core.context.LogContext

/**
 * Created by Emmanuel Requier on 15/12/2020.
 */
abstract class LogEnvironmentFactory {

    abstract suspend fun suspendingLogEnvironment(
        logContext: LogContext,
        collector: LogCollector?
    ): LogEnvironment

    abstract fun blockingLogEnvironment(
        logContext: LogContext,
        collector: LogCollector?
    ): LogEnvironment

    suspend inline fun <reified T> log(logContext: LogContext = EmptyLogContext(),
                                       collector: LogCollector? = null,
                                       vararg params: Any? = arrayOf(),
                                       crossinline body :suspend ()->T) : T {
        val logEnvironment = suspendingLogEnvironment(logContext, collector)
        return logEnvironment.logSuspending<T>(*params){
            body.invoke()
        }
    }
    inline fun <reified T> logBlocking(logContext: LogContext = EmptyLogContext(),
                                       collector: LogCollector? = null,
                                       vararg params: Any? = arrayOf(),
                                       body :()->T): T {
        val logEnvironment = blockingLogEnvironment(logContext, collector)
        val result = runCatching {
            logEnvironment.logBlocking(params, body)
        }
        logEnvironment.disposeBlocking()
        return result.getOrThrow()
    }

}