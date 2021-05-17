package com.alaeri.log.core.child

import com.alaeri.log.core.*
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.collector.MissingCollectorException
import com.alaeri.log.core.Log.Tag
import kotlinx.coroutines.currentCoroutineContext

object ChildLogEnvironmentFactory : LogEnvironmentFactory() {

    //internal
    val threadLocal =  ThreadLocal<LogEnvironment>()

    override suspend fun suspendingLogEnvironment(
        tag: Tag,
        collector: LogCollector?
    ): LogEnvironment {
        val currentCoroutineContext = currentCoroutineContext()
        val parentCoroutineLogEnvironment: CoroutineLogEnvironment? =
            currentCoroutineContext[CoroutineLogKey]
        val parentLogEnvironment = parentCoroutineLogEnvironment?.logEnvironment
        val threadLogEnvironment = threadLocal.get()
        val childLogData = if (parentLogEnvironment != null) {
            ChildTag(parentLogEnvironment.tag) + tag
        } else {
            if (threadLogEnvironment != null) {
                ChildTag(threadLogEnvironment.tag) + tag
            } else {
                tag
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
        } ?: throw MissingCollectorException(childLogData)
        return ChildLogEnvironment(childLogData,
            localCollector,
            { threadLocal.set(this) },
            { threadLocal.set(threadLogEnvironment) })
    }

    override fun blockingLogEnvironment(
        tag: Tag,
        collector: LogCollector?
    ): LogEnvironment {
        val threadLogEnvironment : LogEnvironment? = threadLocal.get()
        val logData = if (threadLogEnvironment != null) {
            ChildTag(threadLogEnvironment.tag) + tag
        } else {
            tag
        }
        val logCollector = if (threadLogEnvironment != null) {
            threadLogEnvironment.collector + collector
        } else {
            collector
        } ?: throw MissingCollectorException(logData)
        return ChildLogEnvironment(logData,
            logCollector,
            { threadLocal.set(this) },
            { threadLocal.set(threadLogEnvironment) })
    }
}