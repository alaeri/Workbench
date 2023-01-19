package com.alaeri.log.core.basic

import com.alaeri.log.core.LogEnvironment
import com.alaeri.log.core.LogEnvironmentFactory
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.collector.NoopCollector
import com.alaeri.log.core.Log.Tag
import com.alaeri.log.core.LogScope

/**
 * Created by Emmanuel Requier on 16/12/2020.
 * This is a non relation version of the log environment factory.
 * @see LogEnvironmentFactory
 * @See BasicLogEnvironment
 * This version will not link logContexts together.
 * It can be used for printing messages, for tests, etc...
 */
object BasicEnvironmentFactory : LogEnvironmentFactory() {
    override suspend fun suspendingLogEnvironment(
        tag: Tag,
        collector: LogCollector?,
        scope: LogScope
    ): LogEnvironment  = BasicLogEnvironment(tag, collector ?: NoopCollector)

    override suspend fun suspendingLogEnvironment(
        tag: Tag,
        collector: LogCollector?
    ): LogEnvironment = BasicLogEnvironment(tag, collector ?: NoopCollector)

    override fun blockingLogEnvironment(
        tag: Tag,
        collector: LogCollector?
    ): LogEnvironment = BasicLogEnvironment(tag, collector ?: NoopCollector)

    override fun blockingLogEnvironment(
        tag: Tag,
        collector: LogCollector?,
        logScope: LogScope
    ): LogEnvironment = BasicLogEnvironment(tag, collector ?: NoopCollector)
}