package com.alaeri.log.core.child

import com.alaeri.log.core.*
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.collector.MissingCollectorException
import com.alaeri.log.core.context.LogContext
import kotlinx.coroutines.currentCoroutineContext

object ChildLogEnvironmentFactory : LogEnvironmentFactory() {

    internal val threadLocal =  ThreadLocal<LogEnvironment>()

    override suspend fun suspendingLogEnvironment(
        logContext: LogContext,
        collector: LogCollector?
    ): LogEnvironment {
        val currentCoroutineContext = currentCoroutineContext()
        val parentCoroutineLogEnvironment: CoroutineLogEnvironment? =
            currentCoroutineContext[CoroutineLogKey]
        val parentLogEnvironment = parentCoroutineLogEnvironment?.logEnvironment
        val threadLogEnvironment = threadLocal.get()
        val childLogData = if (parentLogEnvironment != null) {
            ChildLogContext(parentLogEnvironment.context) + logContext
        } else {
            if (threadLogEnvironment != null) {
                ChildLogContext(threadLogEnvironment.context) + logContext
            } else {
                logContext
            }
        }
        //If there is a parentCoroutineLogDataAndCollector use this collector + the local one
        val localCollector = if (parentLogEnvironment != null) {
            parentLogEnvironment.collector + collector
            //Otherwise use the one from the thread + the local one
        } else if (threadLogEnvironment != null) {
            threadLogEnvironment.collector + collector
            //Otherwise the local one
        } else {
            collector
            //If no collector throw
        } ?: throw MissingCollectorException()
        return ChildLogEnvironment(childLogData,
            localCollector,
            { threadLocal.set(this) },
            { threadLocal.set(threadLogEnvironment) })
    }

    override fun blockingLogEnvironment(
        logContext: LogContext,
        collector: LogCollector?
    ): LogEnvironment {
        val threadLogEnvironment : LogEnvironment? = threadLocal.get()
        val logData = if (threadLogEnvironment != null) {
            ChildLogContext(threadLogEnvironment.context) + logContext
        } else {
            logContext
        }
        val logCollector = if (threadLogEnvironment != null) {
            threadLogEnvironment.collector + collector
        } else {
            collector
        } ?: throw MissingCollectorException()
        return ChildLogEnvironment(logData,
            logCollector,
            { threadLocal.set(this) },
            { threadLocal.set(threadLogEnvironment) })
    }
}