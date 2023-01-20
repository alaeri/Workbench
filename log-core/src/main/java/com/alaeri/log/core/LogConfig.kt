package com.alaeri.log.core

import com.alaeri.log.core.basic.BasicEnvironmentFactory
import com.alaeri.log.core.child.ChildLogEnvironmentFactory
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
    var logEnvironmentFactory : LogEnvironmentFactory = ChildLogEnvironmentFactory
    var defaultCollector: LogCollector? = null

    suspend inline fun <reified T> log(tag: Log.Tag = EmptyTag(),
                               collector: LogCollector? = null,
                               vararg params: Any? = arrayOf(),
                               crossinline body :suspend LogScope.()->T) : T {
        return logEnvironmentFactory.inlineSuspendLog(tag, collector, *params) {
            body.invoke(this)
        }
    }

    suspend inline fun <reified T> LogScope.log(tag: Log.Tag = EmptyTag(),
                                       collector: LogCollector? = null,
                                       vararg params: Any? = arrayOf(),
                                       crossinline body :suspend LogScope.()->T) : T {
        val logScope : LogScope = this
        return logEnvironmentFactory.logWithScope(tag = tag,
            collector =collector,
            params= params,
            logScope = logScope
            ) {
                body.invoke(this)
        }
    }

//    @Suppress("DEPRECATION")
//    @Deprecated(level = DeprecationLevel.WARNING, message = "@see InlineSuspendErrorRepro.kt", replaceWith = ReplaceWith("log"))
//    suspend inline fun <reified T> inlinedSuspendLog(tag: Log.Tag = EmptyTag(),
//                                                     collector: LogCollector? = null,
//                                                     vararg params: Any? = arrayOf(),
//                                                     crossinline body :suspend ()->T) : T {
//        return logEnvironmentFactory.inlineSuspendLog(tag, collector, *params){
//            body.invoke()
//        }
//    }

    inline fun <reified T> logBlocking(tag: Log.Tag = EmptyTag(),
                                       collector: LogCollector? = null,
                                       vararg params: Any? = arrayOf(),
                                       body :LogScope.()->T): T {
        return logEnvironmentFactory.logBlocking(tag, collector, *params){
            body.invoke(this)
        }
    }


    inline fun <reified T> LogScope.logBlocking(tag: Log.Tag = EmptyTag(),
                                       collector: LogCollector? = null,
                                       vararg params: Any? = arrayOf(),
                                       body : LogScope.()->T): T {
        val logScope: LogScope = this
        return logEnvironmentFactory.logBlockingWithScope(
            tag = tag, collector = collector,
            params = *params, logScope = logScope){
            body.invoke(this)
        }
    }
}