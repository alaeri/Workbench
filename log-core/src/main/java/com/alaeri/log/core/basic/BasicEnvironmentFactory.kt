package com.alaeri.log.core.basic

import com.alaeri.log.core.LogEnvironment
import com.alaeri.log.core.LogEnvironmentFactory
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.collector.NoopCollector
import com.alaeri.log.core.context.LogContext

/**
 * Created by Emmanuel Requier on 16/12/2020.
 */
object BasicEnvironmentFactory : LogEnvironmentFactory() {

    override suspend fun suspendingLogEnvironment(
        logContext: LogContext,
        collector: LogCollector?
    ): LogEnvironment = BasicLogEnvironment(logContext, collector ?: NoopCollector)

    override fun blockingLogEnvironment(
        logContext: LogContext,
        collector: LogCollector?
    ): LogEnvironment = BasicLogEnvironment(logContext, collector ?: NoopCollector)
}