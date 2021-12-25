package com.alaeri.cats.app

import android.util.Log
import com.alaeri.log.android.ui.option.DefaultSerializedEntity
import com.alaeri.log.server.LogServer
import com.alaeri.log.core.LogConfig
import com.alaeri.log.core.child.*
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.collector.NoopCollector
import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.extra.identity.IdentityTransformer
import com.alaeri.log.extra.identity.utils.IdBank
import com.alaeri.log.extra.tag.callsite.CallSiteTag
import com.alaeri.log.extra.tag.callsite.CallSiteTransformer
import com.alaeri.log.extra.tag.coroutine.CoroutineContextSerializer
import com.alaeri.log.extra.tag.coroutine.CoroutineContextTag
import com.alaeri.log.extra.tag.name.NameRepresentation
import com.alaeri.log.extra.tag.name.NamedTag
import com.alaeri.log.extra.tag.receiver.ReceiverTag
import com.alaeri.log.extra.tag.receiver.ReceiverTranformer
import com.alaeri.log.extra.tag.thread.ThreadTag
import com.alaeri.log.extra.tag.thread.ThreadTransformer
import com.alaeri.log.extra.type.TypeTypedTransformer
import com.alaeri.log.repository.GraphRepository
import com.alaeri.log.repository.LogRepository
import com.alaeri.log.serialize.serialize.LogSerializer
import com.alaeri.log.serialize.serialize.mapping.CombinedTagTransformer
import com.alaeri.log.serialize.serialize.mapping.EntityTransformer
import com.alaeri.log.serialize.serialize.mapping.TagTypedSerializer
import com.alaeri.log.serialize.serialize.representation.EntityRepresentation
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.util.*

val idBank = IdBank<IdentityRepresentation>(null){ prev ->
    IdentityRepresentation((prev?.index ?: 0) +1, UUID.randomUUID().toString())
}
val identityTranformer = IdentityTransformer(idBank)
val callSiteTransformer = CallSiteTransformer(identityTranformer)
val typeTypedTransformer = TypeTypedTransformer()
val receiverTransformer = ReceiverTranformer(identityTranformer, typeTypedTransformer)
val threadTransformer = ThreadTransformer(identityTranformer)
val coroutineContextSerializer = CoroutineContextSerializer(identityTranformer)
val namedTransformer =object : TagTypedSerializer<NamedTag, NameRepresentation>(NamedTag::class){
    override fun transform(logData: NamedTag): NameRepresentation {
        return NameRepresentation(idBank.keyOf(logData), logData.name)
    }

}
val combinedLogDataTransformer = CombinedTagTransformer(listOf(
    receiverTransformer, threadTransformer,
    namedTransformer, callSiteTransformer,
    coroutineContextSerializer
), identityTranformer
)

val logSerializer = LogSerializer(
    combinedLogDataTransformer,
    object : EntityTransformer<Any, EntityRepresentation<Any>>(Any::class){
        override fun transform(logData: Any): EntityRepresentation<Any> {
            return DefaultSerializedEntity(logData.javaClass.name)
        }

    },
    identityTranformer
)
val logRepository = LogRepository(logSerializer)
val collector : LogCollector = logRepository

val graphRepository = GraphRepository(logRepository)


object SampleLogServer {
    fun quit() {
        logServer.stop()
    }
    fun start(): Unit {
        logServer.start()
    }
    private val logServer =  LogServer(graphRepository, logRepository)
}
internal suspend inline fun <reified T> Any.log(name: String,
                                 vararg params: Any? = arrayOf(),
                                 crossinline body :suspend ()->T) : T {
    val currentCoroutineContext = currentCoroutineContext()
    val logContext = ReceiverTag(
        this) +
            CoroutineContextTag(currentCoroutineContext) +
            CallSiteTag() +
            ThreadTag() +
            NamedTag(name)
    return LogConfig.log(logContext, collector, *params){
        body.invoke()
    }
}

internal inline fun <reified T> LogOwner.logBlocking(name: String,
                                                vararg params: Any? = arrayOf(),
                                                body :()->T): T {
    val logContext =  ReceiverTag(this) +
            CallSiteTag() +
            ThreadTag() +
            NamedTag(name)
    return LogConfig.logBlocking(logContext, collector, *params){
        body.invoke()
    }
}
internal suspend fun <T> LogOwner.logFlow(name: String,
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
    return LogConfig.log(logContext, collector, *params){
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
        val childLogEnvironment = ChildLogEnvironmentFactory.suspendingLogEnvironment(logSiteContext, collector)
        val childCoroutineContext = CoroutineLogEnvironment(childLogEnvironment)
        childLogEnvironment.logSuspending("test") {
            originalFlow
//                .onStart { log("onStart"){} }
                .onEach { log("onEach", it){} }
//                .onCompletion { log("onCompletion"){} }
                .flowOn(childCoroutineContext).collect {
                    println("named: $name")
                    withContext(originalContext){
                        emit(it)
                    }
                }
        }

    }
}
//TODO: make this work even when there is no parent coroutineContext available but only a blocking context in ThreadLocal
internal fun <T> LogOwner.logBlockingFlow(name: String,
                                     vararg params: Any? = arrayOf(),
                                     body : ()->Flow<T>) : Flow<T>{
    val receiver = this
    val logContext =  ReceiverTag(this) +
            CallSiteTag() +
            ThreadTag() +
            NamedTag(name)
    return LogConfig.logBlocking(logContext, collector, *params){
        val childLogEnvironment = ChildLogEnvironmentFactory.blockingLogEnvironment(logContext,
            collector
        )
        val floww = body.invoke()
        val logEnvContext = CoroutineLogEnvironment(childLogEnvironment)
        suspend fun startFlowLogging(){
            val currentCoroutineContext = currentCoroutineContext()
            val parentCoroutineLogEnvironment = currentCoroutineContext[CoroutineLogKey]
            val parentBlockingLogEnvironment = ChildLogEnvironmentFactory.threadLocal.get()
            val parentEnvironment = parentCoroutineLogEnvironment?.logEnvironment ?: parentBlockingLogEnvironment
            if(parentEnvironment == null ){
                println("receiver: $receiver - $name")
            }
            val parentTag = parentEnvironment.tag
            //supervisorScope {
            val logEnvironment = ChildLogEnvironment(parentTag, parentCoroutineLogEnvironment?.logEnvironment?.collector?:NoopCollector,prepare = {},dispose = {})
            logEnvContext.logEnvironment = logEnvironment
        }
        floww.onEach {
            //println("i");
            Log.d("CATS", "onEach: $it")
            receiver.log("onEach", it){}
        }.flowOn(logEnvContext).onStart {
            Log.d("CATS", "startFlowLogging")
            startFlowLogging()
        }
    }

}
