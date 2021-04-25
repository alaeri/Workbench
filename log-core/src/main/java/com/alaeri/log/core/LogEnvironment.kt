package com.alaeri.log.core

import com.alaeri.log.core.child.CoroutineLogEnvironment
import com.alaeri.log.core.collector.LogCollector
import kotlinx.coroutines.SupervisorJob
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

    abstract val tag: Log.Tag
    abstract val collector: LogCollector

    inline fun <T> logBlocking(
        params: Array<out Any?>,
        body: () -> T
    ): T {
        collector.emit(Log(tag, Log.Message.Starting(params.toList())))
        val result = runCatching {
            body.invoke()
        }
        val message = if (result.isSuccess) {
            Log.Message.Done(result.getOrNull())
        } else {
            Log.Message.Failed(result.exceptionOrNull())
        }
        collector.emit(Log(tag, message))
        return result.getOrThrow()
    }

    suspend fun <T> logSuspending(
        vararg params: Any? = arrayOf(),
        body : suspend ()->T): T {
        val coroutineLogEnvironment = CoroutineLogEnvironment(this)
        return withContext(currentCoroutineContext() + coroutineLogEnvironment + SupervisorJob()){
            collector.emit(Log(tag, Log.Message.Starting(params?.toList() ?: emptyList())))
            val result =
                supervisorScope {
                    runCatching {
                        body.invoke()
                    }
                }

            val message = if(result.isSuccess){
                Log.Message.Done(result.getOrNull())
            }else{
                Log.Message.Failed(result.exceptionOrNull())
            }
            collector.emit(Log(tag, message))
            result.getOrThrow()
        }
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "@see InlineSuspendErrorRepro.kt", replaceWith = ReplaceWith("suspendLog"))
    suspend inline fun <reified T> logInlineSuspending(
        vararg params: Any? = arrayOf(),
        crossinline body : suspend ()->T): T {
        val coroutineLogEnvironment = CoroutineLogEnvironment(this)
        return withContext(currentCoroutineContext() + coroutineLogEnvironment + SupervisorJob()){
            collector.emit(Log(tag, Log.Message.Starting(params.toList())))
            val result =
                supervisorScope {
                    runCatching {
                        body.invoke()
                    }
                }

            val message = if(result.isSuccess){
                Log.Message.Done(result.getOrNull())
            }else{
                Log.Message.Failed(result.exceptionOrNull())
            }
            collector.emit(Log(tag, message))
            result.getOrThrow()
        }
    }

    abstract fun prepare()
    abstract fun dispose()
}

