package com.alaeri.log.core

import com.alaeri.log.core.basic.BasicEnvironmentFactory
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.context.EmptyTag
import kotlinx.coroutines.delay

/**
 * This object is the singleton that should be manipulated by apps to change
 * the runtime behaviour depending on the build type
 *
 * Libs should call logging methods using the inline methods here but should not change
 * the value of the logEnvironmentFactory so they can let the app drive the log levels.
 */
object LogConfig{

    /**
     * Change this
     *
     */
    var logEnvironmentFactory : LogEnvironmentFactory = BasicEnvironmentFactory

    suspend fun <T> log(tag: Log.Tag = EmptyTag(),
                               collector: LogCollector? = null,
                               vararg params: Any? = arrayOf(),
                               body :suspend ()->T) : T {
        delay(100)
        return logEnvironmentFactory.log(tag, collector, *params) {
            body.invoke()
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated(level = DeprecationLevel.WARNING, message = "@see InlineSuspendErrorRepro.kt", replaceWith = ReplaceWith("log"))
    suspend inline fun <reified T> inlinedSuspendLog(tag: Log.Tag = EmptyTag(),
                                                     collector: LogCollector? = null,
                                                     vararg params: Any? = arrayOf(),
                                                     crossinline body :suspend ()->T) : T {
        return logEnvironmentFactory.inlineSuspendLog(tag, collector, *params){
            body.invoke()
        }
    }

    inline fun <reified T> logBlocking(tag: Log.Tag = EmptyTag(),
                                       collector: LogCollector? = null,
                                       vararg params: Any? = arrayOf(),
                                       body :()->T): T {
        return logEnvironmentFactory.logBlocking(tag, collector, *params){
            body.invoke()
        }
    }
}