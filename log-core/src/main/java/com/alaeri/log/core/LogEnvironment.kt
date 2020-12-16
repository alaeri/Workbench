package com.alaeri.log.core

import com.alaeri.log.core.child.CoroutineLogEnvironment
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.context.LogContext
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

/**
 * This class purpose is to be stored in a ThreadLocal variable or a CoroutineContext Element
 * so that it can be retrieved at any time and we can build a logging hierarchy...
 *
 * @see com.alaeri.log.core.context.ChildLogContext
 * @see extensionsKt
 */
abstract class LogEnvironment{

    abstract val context: LogContext
    abstract val collector: LogCollector

    inline fun <T> logBlocking(
        params: Array<out Any?>,
        body: () -> T
    ): T {
        collector.emit(context, LogState.Starting(params.toList()))
        val result = runCatching {
            body.invoke()
        }
        val logState = if (result.isSuccess) {
            LogState.Done(result.getOrNull())
        } else {
            LogState.Failed(result.exceptionOrNull())
        }
        collector.emit(context, logState)
        return result.getOrThrow()
    }

    suspend inline fun <reified T> logSuspending(
        vararg params: Any? = arrayOf(),
        crossinline body : suspend ()->T): T {
        val coroutineLogEnvironment = CoroutineLogEnvironment(this)
        return withContext(currentCoroutineContext() + coroutineLogEnvironment){
            collector.emit(context, LogState.Starting(params.toList()))
            val result = supervisorScope {
                runCatching {
                    body.invoke()
                }
            }
            val logState = if(result.isSuccess){
                LogState.Done(result.getOrNull())
            }else{
                LogState.Failed(result.exceptionOrNull())
            }
            collector.emit(context, logState)
            result.getOrThrow()
        }
    }

    abstract fun prepare()
    abstract fun dispose()
}

