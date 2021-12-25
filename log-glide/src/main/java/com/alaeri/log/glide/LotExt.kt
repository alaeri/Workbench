package com.alaeri.log.glide

import android.util.Log.d
import com.alaeri.log.core.LogConfig
import com.alaeri.log.core.child.ChildLogEnvironment
import com.alaeri.log.core.child.ChildLogEnvironmentFactory
import com.alaeri.log.core.child.CoroutineLogEnvironment
import com.alaeri.log.core.child.CoroutineLogKey
import com.alaeri.log.core.collector.NoopCollector
import com.alaeri.log.extra.tag.callsite.CallSiteTag
import com.alaeri.log.extra.tag.coroutine.CoroutineContextTag
import com.alaeri.log.extra.tag.name.NamedTag
import com.alaeri.log.extra.tag.receiver.ReceiverTag
import com.alaeri.log.extra.tag.thread.ThreadTag
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

/**
 * To use the logger copy this class in your project and extend modify as needed
 *
 * In debug you can set LogConfig.LogEnvironmentFactory = ChildLogEnvironmentFactory
 * To silence logs in prod you can use NoopLogCollector.
 *
 * Created by Emmanuel Requier on 20/12/2020.
 */

internal suspend inline fun <reified T> Any.log(name: String,
                                 vararg params: Any? = arrayOf(),
                                 crossinline body : suspend ()->T) : T {
    d("CATS-IMAGE-LOADER", "log: $name")
    val currentCoroutineContext = currentCoroutineContext()
    val logContext = ReceiverTag(
        this) +
            CoroutineContextTag(currentCoroutineContext) +
            CallSiteTag() +
            ThreadTag() +
            NamedTag(name)
    return LogConfig.log(logContext, null, *params){
        body.invoke()
    }
}

internal inline fun <reified T> Any.logBlocking(name: String,
                                                vararg params: Any? = arrayOf(),
                                                body :()->T): T {
    val logContext =  ReceiverTag(this) +
            CallSiteTag() +
            ThreadTag() +
            NamedTag(name)
    return LogConfig.logBlocking(logContext, null, *params){
        body.invoke()
    }
}
internal suspend fun <T> Any.logFlow(name: String,
                                     vararg params: Any? = arrayOf(),
                                     body :suspend ()->Flow<T>) : Flow<T> {
    val receiver = this
    val currentCoroutineContext = currentCoroutineContext()
    val logContext = ReceiverTag(
        this) +
            CoroutineContextTag(currentCoroutineContext) +
            CallSiteTag() +
            ThreadTag() +
            NamedTag(name)
    return LogConfig.log(logContext, null, *params){
        val floww = body.invoke()
        floww.log(name, params)
    }
}
fun <T> Flow<T>.log(name: String,
                    vararg params: Any? = arrayOf()): Flow<T>{
    val logSiteContext =
        ReceiverTag(this) +
                //CoroutineContextTag(currentCoroutineContext) +
                CallSiteTag() +
                ThreadTag() +
                NamedTag(name)
    val originalFlow = this
    return flow<T> {
        val originalContext = currentCoroutineContext()
        val childLogEnvironment = ChildLogEnvironmentFactory.suspendingLogEnvironment(logSiteContext, null)
        val childCoroutineContext = CoroutineLogEnvironment(childLogEnvironment)
        childLogEnvironment.logSuspending("test") {
            originalFlow
//                .onStart { log("onStart"){} }
                .onEach { log("onEach", it){} }
//                .onCompletion { log("onCompletion"){} }
                .flowOn(childCoroutineContext).collect {
                    withContext(originalContext){
                        emit(it)
                    }
                }
        }

    }
}
internal fun <T> Any.logBlockingFlow(name: String,
                                     vararg params: Any? = arrayOf(),
                                     body : ()->Flow<T>) : Flow<T>{
    val receiver = this
    val logContext =  ReceiverTag(this) +
            CallSiteTag() +
            ThreadTag() +
            NamedTag(name)
    return LogConfig.logBlocking(logContext, null, *params){
        val childLogEnvironment = ChildLogEnvironmentFactory.blockingLogEnvironment(logContext,null)
        val floww = body.invoke()
        val logEnvContext = CoroutineLogEnvironment(childLogEnvironment)
        suspend fun startFlowLogging(){
            val currentCoroutineContext = currentCoroutineContext()
            val parentLogEnvironment = currentCoroutineContext[CoroutineLogKey]
            val parentTag = parentLogEnvironment!!.logEnvironment.tag
            //supervisorScope {
            val logEnvironment = ChildLogEnvironment(parentTag, parentLogEnvironment?.logEnvironment?.collector?:NoopCollector,prepare = {},dispose = {})
            logEnvContext.logEnvironment = logEnvironment
        }
        floww.onEach {
            //println("i");
            d("CATS-IMAGE-LOADER", "onEach: $it")
            receiver.log("onEach", it){}
        }.flowOn(logEnvContext).onStart {
            d("CATS-IMAGE-LOADER", "onEach: ${startFlowLogging()}")
            startFlowLogging()
        }
    }

}
