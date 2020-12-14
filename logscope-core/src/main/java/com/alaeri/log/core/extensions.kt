package com.alaeri.log.core
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.context.ChildLogContext
import com.alaeri.log.core.context.EmptyLogContext
import com.alaeri.log.core.context.LogContext
import kotlinx.coroutines.*

suspend inline fun <reified T> log(logContext: LogContext = EmptyLogContext(),
                                   collector: LogCollector? = null,
                                   vararg params: Any? = arrayOf(),
                                   crossinline body :suspend ()->T) : T{
    val currentCoroutineContext = currentCoroutineContext()
    val parentCoroutineLogContext : CoroutineLogContext? = currentCoroutineContext[CoroutineLogKey]
    val parentCoroutineLogDataAndCollector = parentCoroutineLogContext?.logEnvironment
    val threadLocal = BlockingLogContextStore.threadLocal
    val threadLogDataAndCollector = threadLocal.get()
    val childLogData = if(parentCoroutineLogDataAndCollector !=null){
        ChildLogContext(parentCoroutineLogDataAndCollector.context) + logContext
    }else{
        if(threadLogDataAndCollector != null){
            ChildLogContext(threadLogDataAndCollector.context) + logContext
        }else{
            logContext
        }
    }
    //If there is a parentCoroutineLogDataAndCollector use this collector + the local one
    val localCollector = if(parentCoroutineLogDataAndCollector != null){
        parentCoroutineLogDataAndCollector.collector + collector
    //Otherwise use the one from the thread + the local one
    }else if( threadLogDataAndCollector != null){
        threadLogDataAndCollector.collector + collector
    //Otherwise the local one
    }else{
        collector
        //If no collector throw
    } ?: throw MissingCollectorException()

    val logMetadataAndCollector = LogEnvironment(childLogData, localCollector)
    val childCoroutineLogContext = CoroutineLogContext(logMetadataAndCollector)
    return withContext(currentCoroutineContext() + childCoroutineLogContext){
        logMetadataAndCollector.collector.emit(logMetadataAndCollector.context, LogState.Starting(params.toList()))
        val result = supervisorScope {
            runCatching {
                body()
            }
        }
        val logState = if(result.isSuccess){
            LogState.Done(result.getOrNull())
        }else{
            LogState.Failed(result.exceptionOrNull())
        }
        logMetadataAndCollector.collector.emit(logMetadataAndCollector.context, logState)
        result.getOrThrow()
    }
}

inline fun <reified T> logBlocking(logContext: LogContext = EmptyLogContext(),
                                   collector: LogCollector? = null,
                                   vararg params: Any? = arrayOf(),
                                   body :()->T): T {
    val threadLocal = BlockingLogContextStore.threadLocal
    val threadLogEnvironment = threadLocal.get()
    val logData = if(threadLogEnvironment !=null){
        ChildLogContext(threadLogEnvironment.context) + logContext
    }else{
        logContext
    }
    val logCollector = if(threadLogEnvironment != null){
        threadLogEnvironment.collector + collector
    }else{
        collector
    } ?: throw MissingCollectorException()
    val logEnvironment = LogEnvironment(logData, logCollector)
    threadLocal.set(logEnvironment)
    logEnvironment.collector.emit(logEnvironment.context, LogState.Starting(params.toList()))
    val result = runCatching {
        body.invoke()
    }
    val logState = if(result.isSuccess){
        LogState.Done(result.getOrNull())
    }else{
        LogState.Failed(result.exceptionOrNull())
    }
    logEnvironment.collector.emit(logEnvironment.context, logState)
    threadLocal.set(threadLogEnvironment)
    return result.getOrThrow()
}

