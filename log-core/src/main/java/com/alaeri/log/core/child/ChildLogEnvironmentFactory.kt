package com.alaeri.log.core.child

import com.alaeri.log.core.*
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.collector.MissingCollectorException
import com.alaeri.log.core.Log.Tag
import kotlinx.coroutines.currentCoroutineContext

object ChildLogEnvironmentFactory : LogEnvironmentFactory() {

    //internal
    val threadLocal =  ThreadLocal<LogEnvironment>().also {
        println("threadLocalInitialized.....")
    }

    override suspend fun suspendingLogEnvironment(
        tag: Tag,
        collector: LogCollector?,
        scope: LogScope
    ): LogEnvironment {
            val scopeLogEnvironment = scope.logEnvironment
            val childLogData = ChildTag(scopeLogEnvironment.tag) + tag
            //If there is a parentCoroutineLogDataAndCollector use this collector + the local one
            val localCollector = scopeLogEnvironment.collector + collector
            return ChildLogEnvironment(childLogData,
                localCollector,
                {},
                {}
            )
        }

    override fun blockingLogEnvironment(
        tag: Tag,
        collector: LogCollector?,
        logScope: LogScope
    ): LogEnvironment {
        val scopeLogEnvironment : LogEnvironment? = logScope.logEnvironment
        val logData = if (scopeLogEnvironment != null) {
            ChildTag(scopeLogEnvironment.tag) + tag
        } else {
            tag
        }
        val logCollector = if (scopeLogEnvironment != null) {
            scopeLogEnvironment.collector + collector
        } else {
            collector
        } ?: throw MissingCollectorException(logData)
        return ChildLogEnvironment(logData,
            logCollector,
            {},
            {})
    }

    override suspend fun suspendingLogEnvironment(
        tag: Tag,
        collector: LogCollector?,
    ): LogEnvironment {
        val currentCoroutineContext = currentCoroutineContext()
        val parentCoroutineLogEnvironment: CoroutineLogEnvironment? =
            currentCoroutineContext[CoroutineLogKey]
        val parentLogEnvironment = parentCoroutineLogEnvironment?.logEnvironment
        val threadLogEnvironment = threadLocal.get()
        println("threadLocal: $threadLocal get: $threadLogEnvironment in suspendingLogEnvironment +${Thread.currentThread().name}")
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
            { threadLocal.set(this@ChildLogEnvironment)
                println("threadLocal: $threadLocal with: ${this@ChildLogEnvironment}")
            },
            { threadLocal.set(threadLogEnvironment)
                println("threadLocal: $threadLocal with: $threadLogEnvironment")
            })
    }

    override fun blockingLogEnvironment(
        tag: Tag,
        collector: LogCollector?
    ): LogEnvironment {
        val threadLogEnvironment : LogEnvironment? = threadLocal.get()
        println("threadLocal: $threadLocal get: $threadLogEnvironment in blockingLogEnvironnment +${Thread.currentThread().name}")
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
            { threadLocal.set(this)
                println("threadLocal: $threadLocal set to: ${this@ChildLogEnvironment}")
            },
            { threadLocal.set(threadLogEnvironment)
                println("threadLocal: $threadLocal unset to: $threadLogEnvironment")
            })
    }
}