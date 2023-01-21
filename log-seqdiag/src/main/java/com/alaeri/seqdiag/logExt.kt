package com.alaeri.seqdiag

import com.alaeri.log.core.*
import com.alaeri.log.core.LogConfig.log
import com.alaeri.log.core.LogConfig.logBlocking
import com.alaeri.log.core.child.*
import com.alaeri.log.core.collector.LogCollector
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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * To use the logger copy this class in your project and extend modify as needed
 *
 * In debug you can set LogConfig.LogEnvironmentFactory = ChildLogEnvironmentFactory
 * To silence logs in prod you can use NoopLogCollector.
 *
 * Created by Emmanuel Requier on 20/12/2020.
 */
val idBank = IdBank<IdentityRepresentation>(null){ prev ->
    IdentityRepresentation(if(prev != null) prev.index + 1 else  0, UUID.randomUUID().toString())
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

            return object : EntityRepresentation<Any>{
                override fun toString(): String {
                    return logData.toString()
                }
            }
        }

    },
    identityTranformer
)
val logRepository = LogRepository(logSerializer)
val collector : LogCollector = logRepository

val graphRepository = GraphRepository(logRepository)


internal suspend inline fun <reified T> log(name: String,
                                            receiverTag: ReceiverTag,
                                                vararg params: Any? = arrayOf(),
                                                crossinline body :suspend LogScope.()->T) : T {
    val currentCoroutineContext = currentCoroutineContext()
    val logContext = receiverTag +
            CoroutineContextTag(currentCoroutineContext) +
            CallSiteTag() +
            ThreadTag() +
            NamedTag(name)
    return LogConfig.log(logContext, collector, *params){
        body.invoke(this)
    }
}
internal suspend inline fun <reified T> LogScope.log(name: String,
                                                     receiverTag: ReceiverTag,
                                                vararg params: Any? = arrayOf(),
                                                crossinline body :suspend LogScope.()->T) : T {
    val currentCoroutineContext = currentCoroutineContext()
    val logContext =
            receiverTag +
            CoroutineContextTag(currentCoroutineContext) +
            CallSiteTag() +
            ThreadTag() +
            NamedTag(name)
    val logScope = this
    return logScope.log(tag = logContext, collector = collector, params = *params){
        body.invoke(this)
    }
}

internal inline fun <reified T> logBlocking(name: String,
                                            receiverTag: ReceiverTag,
                                            vararg params: Any? = arrayOf(),
                                            body : LogScope.()->T): T {
    val logContext =  receiverTag +
            CallSiteTag() +
            ThreadTag() +
            NamedTag(name)
    return LogConfig.logBlocking(logContext, collector, *params){
        body.invoke(this)
    }
}

internal inline fun <reified T> LogScope.logBlocking(name: String,
                                                     receiverTag: ReceiverTag,
                                                     vararg params: Any? = arrayOf(),
                                                     body : LogScope.()->T): T {
    val logContext =  receiverTag +
            CallSiteTag() +
            ThreadTag() +
            NamedTag(name)
    val logScope: LogScope = this
    return logScope.logBlocking(logContext, collector, *params){
        body.invoke(this)
    }
}
//internal suspend fun <T> Any.logFlow(name: String,
//                                     vararg params: Any? = arrayOf(),
//                                     body :suspend ()->Flow<T>) : Flow<T> {
//    val receiver = this
//    val currentCoroutineContext = currentCoroutineContext()
//    val logContext = ReceiverTag(
//        this) +
//            CoroutineContextTag(currentCoroutineContext) +
//            CallSiteTag() +
//            ThreadTag() +
//            NamedTag(name)
//    return LogConfig.log(logContext, collector, *params){
//        val floww = body.invoke()
//        floww.log(name, params)
//    }
//}
fun <T> Flow<T>.log(name: String,
                    receiverTag: ReceiverTag,
                    vararg params: Any? = arrayOf()): Flow<T>{
    val logSiteContext =
       receiverTag +
                //CoroutineContextTag(currentCoroutineContext()) +
                //CallSiteTag() +
                //ThreadTag() +
                NamedTag(name)
    val originalFlow = this
    return flow {
        delay(100)
        val childFlowCollector = this@flow
        val originalContext = currentCoroutineContext()
        println("$name flow coroutine context: $originalContext")
        val logEnvironment = LogConfig.logEnvironmentFactory.suspendingLogEnvironment(logSiteContext, collector)
        //val childLogEnvironment = ChildLogEnvironmentFactory.suspendingLogEnvironment(logSiteContext, collector)
        val childCoroutineContext = CoroutineLogEnvironment(logEnvironment)
        val cf = //log(name, receiverTag, collector)
                run {
            val zig = childCoroutineContext
            val zigMinJob = zig.minusKey(Job)
            println("$name zigMinJob: $zigMinJob")
            channelFlow {
                originalFlow
                    .onStart {
                        logEnvironment.logFlowEvent(Log.Message.Starting(listOf()))
                    }
                    .onEach {
                        logEnvironment.logFlowEvent(Log.Message.OnEach(it))
                    }
                    .onCompletion {
                        if(it != null){
                            logEnvironment.logFlowEvent(Log.Message.Failed(it))
                        }else{
                            logEnvironment.logFlowEvent(Log.Message.Done<Unit>(Unit))
                        }

                    }
                    .flowOn(zigMinJob)
                    .collectLatest{ item ->
                        trySend(item)
                        println("sent cf")
                    }
            }
        }
        cf.collect {
            emit(it)
            println("emitted f")
        }
    }
}
fun <T> Flow<T>.logShareIn(name: String,
                    receiverTag: ReceiverTag,
                    vararg params: Any? = arrayOf(),
                    coroutineScope: CoroutineScope,
                    sharingStarted: SharingStarted,
                    replayCount: Int
): SharedFlow<T>{
    class LogSharedFlow<T>(originalFlow: Flow<T>): SharedFlow<T>{

        val innerLogEnv = LogConfig.logEnvironmentFactory.blockingLogEnvironment(NamedTag("$name-inner")+receiverTag, collector)
        val innerLogCoroutineContext : CoroutineLogEnvironment = CoroutineLogEnvironment(innerLogEnv)
        val innerSharedFlow: SharedFlow<T> = originalFlow
            .flowOn(innerLogCoroutineContext)
            .onStart {
                innerLogEnv.logFlowEvent(Log.Message.Starting(listOf()))
            }
            .onEach {
                innerLogEnv.logFlowEvent(Log.Message.OnEach(it))
            }
            .onCompletion {
                if(it != null){
                    innerLogEnv.logFlowEvent(Log.Message.Failed(it))
                }else{
                    innerLogEnv.logFlowEvent(Log.Message.Done<Unit>(Unit))
                }
            }
            .flowOn(innerLogCoroutineContext)
            .shareIn(coroutineScope, sharingStarted, replayCount)
        val logCollector = collector

        override val replayCache: List<T>
            get() = innerSharedFlow.replayCache

        override suspend fun collect(collector: FlowCollector<T>): Nothing {
           val outerLogEnvironment : LogEnvironment = ChildLogEnvironmentFactory.suspendingLogEnvironmentWithP(
               NamedTag("$name-outer")+receiverTag,
               logCollector,
               ParentTag(innerLogEnv.tag),
           )
            val outerLogCoroutineContext = CoroutineLogEnvironment(outerLogEnvironment)
            innerSharedFlow
                .onStart {
                    outerLogEnvironment.logFlowEvent(Log.Message.Starting(listOf()))
                }
                .onEach {
                    outerLogEnvironment.logFlowEvent(Log.Message.OnEach(it))
                }
                .onCompletion {
                    if(it != null){
                        outerLogEnvironment.logFlowEvent(Log.Message.Failed(it))
                    }else{
                        outerLogEnvironment.logFlowEvent(Log.Message.Done<Unit>(Unit))
                    }
                }
                .flowOn(outerLogCoroutineContext).collect(collector)
            throw CancellationException()
        }
    }
    return LogSharedFlow(this)
}
