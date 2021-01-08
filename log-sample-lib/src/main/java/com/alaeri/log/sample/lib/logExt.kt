package com.alaeri.log.sample.lib

import com.alaeri.log.core.Log
import com.alaeri.log.core.LogConfig
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.collector.LogPrinter
import com.alaeri.log.core.collector.NoopCollector
import com.alaeri.log.extra.tag.callsite.CallSiteTag
import com.alaeri.log.extra.tag.coroutine.CoroutineContextTag
import com.alaeri.log.extra.tag.name.NamedTag
import com.alaeri.log.extra.tag.receiver.ReceiverTag
import com.alaeri.log.extra.tag.thread.ThreadTag
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

/**
 * To use the logger copy this class in your project and extend modify as needed
 *
 * In debug you can set LogConfig.LogEnvironmentFactory = ChildLogEnvironmentFactory
 * To silence logs in prod you can use NoopLogCollector.
 *
 * Created by Emmanuel Requier on 20/12/2020.
 */
val collector = object: LogCollector{
    override fun emit(log: Log) {
        "HERE GOES NOTHING"
    }
}
internal suspend inline fun <reified T> Any.logLib(name: String,
                                   vararg params: Any? = arrayOf(),
                                   crossinline body :suspend ()->T) : T {
    val currentCoroutineContext = currentCoroutineContext()
    val logContext = CoroutineContextTag(currentCoroutineContext) +
            CallSiteTag() +
            ReceiverTag(this) +
            NamedTag(name) +
            ThreadTag()

    return LogConfig.log(logContext, collector, *params){
        body.invoke()
    }
}

internal inline fun <reified T> Any.logBlockingLib(name: String,
                                   vararg params: Any? = arrayOf(),
                                   body :()->T): T {
    val logContext =  CallSiteTag() +
            ReceiverTag(this) +
            NamedTag(name) +
            ThreadTag()
    return LogConfig.logBlocking(logContext, collector, *params){
        body.invoke()
    }
}
internal inline fun <reified T> Any.logFlow(name: String,
                                            vararg params: Any? = arrayOf(),
                                            flowBuilder: ()-> Flow<T>
) : Flow<T> {
    val logTag =  CallSiteTag() +
            ReceiverTag(this) +
            ThreadTag() +
            NamedTag(name)
    val receiver  = this
    return LogConfig.logBlocking (logTag, collector, *params){
        flowBuilder.invoke().onEach { receiver.logLib("$name:emit", it){} }
    }
}
