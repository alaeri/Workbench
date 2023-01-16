package com.alaeri.log.sample

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
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.util.*

/**
 * To use the logger copy this class in your project and extend modify as needed
 *
 * In debug you can set LogConfig.LogEnvironmentFactory = ChildLogEnvironmentFactory
 * To silence logs in prod you can use NoopLogCollector.
 *
 * Created by Emmanuel Requier on 20/12/2020.
 */
val idBank = IdBank<IdentityRepresentation>(null){ prev ->
    IdentityRepresentation(prev?.index ?: 0, UUID.randomUUID().toString())
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
    coroutineContextSerializer), identityTranformer)

val logSerializer = LogSerializer(
    combinedLogDataTransformer,
    object : EntityTransformer<Any, EntityRepresentation<Any>>(Any::class){
        override fun transform(logData: Any): EntityRepresentation<Any> {
            return object : EntityRepresentation<Any>{}
        }

    },
    identityTranformer)
val logRepository = LogRepository(logSerializer)
val collector : LogCollector = logRepository

val graphRepository = GraphRepository(logRepository)


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

internal inline fun <reified T> Any.logBlocking(name: String,
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
    return LogConfig.log(logContext, collector, *params){
        val floww = body.invoke()
        floww.log(name, params)
    }
}
fun <T> Flow<T>.log(name: String,
                    vararg params: Any? = arrayOf()): Flow<T>{
    val logSiteContext =
        ReceiverTag(this) +
                //CoroutineContextTag(currentCoroutineContext()) +
                CallSiteTag() +
                ThreadTag() +
                NamedTag(name)
    val originalFlow = this
    return channelFlow<T> {->
        val childFlowCollector = this@channelFlow
        val originalContext = currentCoroutineContext()
        val childLogEnvironment = ChildLogEnvironmentFactory.suspendingLogEnvironment(logSiteContext, collector)
        val childCoroutineContext = CoroutineLogEnvironment(childLogEnvironment)
        childLogEnvironment.logSuspending("test") {
            originalFlow
                .flowOn(childCoroutineContext)
                .onStart { log("onStart"){} }
                .onEach { log("onEach", it){} }
                .onCompletion { log("onCompletion"){} }

//            .flowOn(originalContext)
                .collectLatest{
                    childFlowCollector.trySend(it)
                }
        }


    }
}
