package com.alaeri.log.core

import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.context.LogContext

/**
 * This class purpose is to be stored in a ThreadLocal variable or a CoroutineContext Element
 * so that it can be retrieved at any time and we can build a logging hierarchy...
 *
 * @see com.alaeri.log.core.context.ChildLogContext
 * @see extensionsKt
 */
data class LogEnvironment(
    val context: LogContext,
    val collector: LogCollector
)