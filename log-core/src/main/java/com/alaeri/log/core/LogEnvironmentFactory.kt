package com.alaeri.log.core

import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.context.EmptyTag
import com.alaeri.log.core.Log.Tag

/**
 * Created by Emmanuel Requier on 15/12/2020.
 */
abstract class LogEnvironmentFactory {

    abstract suspend fun suspendingLogEnvironment(
        tag: Tag,
        collector: LogCollector?
    ): LogEnvironment

    abstract fun blockingLogEnvironment(
        tag: Tag,
        collector: LogCollector?
    ): LogEnvironment

    suspend inline fun <reified T> log(tag: Tag = EmptyTag(),
                                       collector: LogCollector? = null,
                                       vararg params: Any? = arrayOf(),
                                       crossinline body :suspend ()->T) : T {
        val logEnvironment = suspendingLogEnvironment(tag, collector)
        logEnvironment.prepare()
        val result = kotlin.runCatching {
            logEnvironment.logSuspending<T>(*params){
                body.invoke()
            }
        }
        logEnvironment.dispose()
        return result.getOrThrow()
    }
    inline fun <reified T> logBlocking(tag: Tag = EmptyTag(),
                                       collector: LogCollector? = null,
                                       vararg params: Any? = arrayOf(),
                                       body :()->T): T {
        val logEnvironment = blockingLogEnvironment(tag, collector)
        logEnvironment.prepare()
        val result = runCatching {
            logEnvironment.logBlocking(params, body)
        }
        logEnvironment.dispose()
        return result.getOrThrow()
    }

}