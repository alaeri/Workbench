package com.alaeri.logscope.core
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class LogPrinter: LogCollector{
    override fun emit(logMetadata: LogMetadata<*>, logState: LogState) {
        println("logContext: $logMetadata -> logState: $logState")
    }
}
sealed class LogState{
    data class Starting(val params: List<Any> = emptyList()): LogState()
    data class Done<T>(val result: T): LogState()
    data class Failed(val exception: Throwable?): LogState()
}
interface LogCollector: LogContext{
    fun emit(logMetadata: LogMetadata<*>, logState: LogState)

    infix operator fun plus(other: LogContext): LogCollector {
        return when(other){
            is LogMetadataAndCollector<*> -> LogMetadataAndCollector(other.logMetadata, this + other.logCollector)
            is LogMetadata<*> -> LogMetadataAndCollector(other, this)
            is LogCollector -> CombinedLogCollector(this, other)
            else -> this
        }
    }
}
interface LogRepresentation{

}
interface LogMetadata<T : LogRepresentation>: LogContext{
    infix operator fun plus(other: LogContext): LogMetadata<*> {
        return when(other){
            is LogMetadataAndCollector<*> -> LogMetadataAndCollector(this + other, other.logCollector)
            is LogMetadata<*> -> CombinedLogMetadata<T>(this, other)
            is LogCollector -> LogMetadataAndCollector<T>(this, other)
            else -> this
        }
    }
}
data class LogMetadataAndCollector<T: LogRepresentation>(
    val logMetadata: LogMetadata<T>,
    val logCollector: LogCollector) : LogMetadata<T> by logMetadata, LogCollector by logCollector{

    override infix operator fun plus(other: LogContext): LogMetadataAndCollector<*> {
        return when(other){
            is LogMetadataAndCollector<*> -> LogMetadataAndCollector(logMetadata + other, logCollector + other)
            is LogMetadata<*> -> LogMetadataAndCollector(logMetadata + other, logCollector)
            is LogCollector -> LogMetadataAndCollector(logMetadata, logCollector + other)
            else -> this
        }
    }
}
class CombinedRepresentation<T: LogRepresentation>(
    val left: T,
    val right: LogRepresentation): LogRepresentation{
}
class EmptyLogRepresentation: LogRepresentation
object EmptyLogMetadata: LogMetadata<EmptyLogRepresentation>{

}
interface LogContext
data class CombinedLogMetadata<T: LogRepresentation>(
    val left : LogMetadata<T>,
    val right: LogMetadata<*>) : LogMetadata<CombinedRepresentation<T>>{
}
data class CombinedLogCollector(val logCollector: LogCollector, val other: LogCollector): LogCollector{
    override fun emit(logMetadata: LogMetadata<*>, logState: LogState) {
        logCollector.emit(logMetadata, logState)
        other.emit(logMetadata, logState)
    }
}
class CoroutineLogContext(val logMetadataAndCollector: LogMetadataAndCollector<*>) : CoroutineContext.Element{
    override val key: CoroutineContext.Key<*> = LogKey
}
object LogKey: CoroutineContext.Key<CoroutineLogContext>{

}
class MissingCollectorException: Exception("no collector defined")
suspend inline fun <reified T> logScope(logContext: LogContext = EmptyLogMetadata,
                                        crossinline body :suspend ()->T) : T{
    val currentCoroutineContext = currentCoroutineContext()
    val parentCoroutineLogContext : CoroutineLogContext? = currentCoroutineContext[LogKey]
    val logMetadataAndCollector : LogMetadataAndCollector<*> = (if(parentCoroutineLogContext != null){
        parentCoroutineLogContext.logMetadataAndCollector + logContext
    }else{
        logContext as? LogMetadataAndCollector<*>
    }) ?: throw MissingCollectorException()
    val childCoroutineLogContext = CoroutineLogContext(logMetadataAndCollector)
    return withContext(currentCoroutineContext() + childCoroutineLogContext){
        logMetadataAndCollector.logCollector.emit(logMetadataAndCollector.logMetadata, LogState.Starting())
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
        logMetadataAndCollector.logCollector.emit(logMetadataAndCollector.logMetadata, logState)
        result.getOrThrow()
    }
}
class CallSiteRepresentation: LogRepresentation
class CallSiteLogMetadata(val stackTraceElements : Array<StackTraceElement> = Thread.currentThread().stackTrace): LogMetadata<CallSiteRepresentation>
class ClassInstanceRepresentation: LogRepresentation
class ObjectLogMetadata(val any: Any): LogMetadata<ClassInstanceRepresentation>
class NameRepresentation: LogRepresentation
class NamedLogMetadata(val name: String): LogMetadata<NameRepresentation>
class ThreadRepresentation: LogRepresentation
class ThreadLogMetadata(val thread: Thread = Thread.currentThread()): LogMetadata<ThreadRepresentation>


fun Any.buildDefaultLogContext(name: String): LogMetadata<*> {
    return CallSiteLogMetadata()+ ObjectLogMetadata(this) + NamedLogMetadata(name) + ThreadLogMetadata()
}

inline fun <reified T> blockingLogScope(logMetadata: LogContext = EmptyLogMetadata,
                                        body :()->T): T {
    val threadLocal = BlockingLogContextStore.threadLocal
    val parentElement = threadLocal.get()
    val childLogElement = if(parentElement !=null){
        parentElement + logMetadata
    }else{
        logMetadata
    } as? LogMetadataAndCollector<*> ?: throw MissingCollectorException()
    threadLocal.set(childLogElement)
    childLogElement.logCollector.emit(childLogElement.logMetadata, LogState.Starting())
    val result = runCatching {
        body.invoke()
    }
    val logState = if(result.isSuccess){
        LogState.Done(result.getOrNull())
    }else{
        LogState.Failed(result.exceptionOrNull())
    }
    childLogElement.logCollector.emit(childLogElement.logMetadata, logState)
    threadLocal.set(parentElement)
    return result.getOrThrow()
}

object BlockingLogContextStore{

    val threadLocal = ThreadLocal<LogMetadataAndCollector<*>?>()


}

