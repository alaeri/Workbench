package com.alaeri.log.core

import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.context.EmptyTag
import com.alaeri.log.core.Log.Tag

class LogScope(val logEnvironment: LogEnvironment)
/**
 * Created by Emmanuel Requier on 15/12/2020.
 */
abstract class LogEnvironmentFactory {
    abstract suspend fun suspendingLogEnvironment(
        tag: Tag,
        collector: LogCollector?,
        scope: LogScope
    ): LogEnvironment

    abstract suspend fun suspendingLogEnvironment(
        tag: Tag,
        collector: LogCollector?
    ): LogEnvironment

    abstract fun blockingLogEnvironment(
        tag: Tag,
        collector: LogCollector?
    ): LogEnvironment

    abstract fun blockingLogEnvironment(
        tag: Tag,
        collector: LogCollector?,
        logScope: LogScope
    ): LogEnvironment

    suspend inline fun <reified T> inlineSuspendLog(tag: Tag = EmptyTag(),
                                                    collector: LogCollector? = null,
                                                    vararg params: Any? = arrayOf(),
                                                    crossinline body :suspend LogScope.()->T) : T {
        val logEnvironment = suspendingLogEnvironment(tag, collector)
        val logScope = LogScope(logEnvironment)
        logEnvironment.prepare()
        val result = kotlin.runCatching {
            logEnvironment.logInlineSuspending2<T>(*params){
                body.invoke(logScope)
            }
        }
        logEnvironment.dispose()
        return result.getOrThrow()
    }
    inline fun <reified T> logBlocking(tag: Tag = EmptyTag(),
                                       collector: LogCollector? = null,
                                       vararg params: Any? = arrayOf(),
                                       body :LogScope.()->T): T {
        val logEnvironment = blockingLogEnvironment(tag, collector)
        val logScope = LogScope(logEnvironment)
        logEnvironment.prepare()
        val result = runCatching {
            logEnvironment.logBlocking(params, body, logScope = logScope)
        }
        logEnvironment.dispose()
        return result.getOrThrow()
    }

    inline fun <reified T> logBlockingWithScope(tag: Tag = EmptyTag(),
                                        collector: LogCollector? = null,
                                        vararg params: Any? = arrayOf(),
                                        logScope: LogScope,
                                        body: LogScope.()-> T): T
    {
        val logEnvironment = blockingLogEnvironment(tag, collector, logScope)
        val scope = LogScope(logEnvironment)
        logEnvironment.prepare()
        val result = runCatching {
            logEnvironment.logBlocking(params, body, scope)
        }
        logEnvironment.dispose()
        return result.getOrThrow()
    }

    suspend inline fun <reified T> logWithScope(tag: Tag = EmptyTag(),
                                                collector: LogCollector? = null,
                                                vararg params: Any? = arrayOf(),
                                                logScope: LogScope,
                                                crossinline body: suspend LogScope.()-> T): T
    {
        val logEnvironment = suspendingLogEnvironment(tag, collector, logScope)
        logEnvironment.prepare()
        val result = runCatching<T> {
            logEnvironment.logInlineSuspending(
                logScope = logScope,
                body = body,
                params = params)
        }
        logEnvironment.dispose()
        return result.getOrThrow()
    }


}