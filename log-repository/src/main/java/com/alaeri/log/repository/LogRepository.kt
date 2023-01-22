package com.alaeri.log.repository

import com.alaeri.log.core.Log
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.extra.tag.receiver.ReceiverRepresentation
import com.alaeri.log.serialize.serialize.EmptySerializedTag
import kotlinx.coroutines.flow.MutableStateFlow
import com.alaeri.log.serialize.serialize.LogSerializer
import com.alaeri.log.serialize.serialize.SerializedLog
import com.alaeri.log.serialize.serialize.SerializedTag
import com.alaeri.log.serialize.serialize.representation.EntityRepresentation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlin.coroutines.CoroutineContext


//Maybe use GumTree to diff the trees
/**
 * This file is a scratch file to elaborate the best visualisation of the program's execution
 * Stack Receivers by class
 *   Stack Logs by receiver instance
 *                 parent log
 *
 *
 *
 */
class LogRepository(private val logSerializer: LogSerializer,
                    private val context: CoroutineContext = newSingleThreadContext("repository"),
                    private val logScope : CoroutineScope = CoroutineScope(context)
) : LogCollector {

    val listAsFlow = MutableStateFlow<List<SerializedLog<IdentityRepresentation>>>(listOf())
    val mapAsFlow: Flow<Map<SerializedTag, LogState>> = listAsFlow.map { it.fold(mapOf()){ acc, _ ->
            acc
        }
    }
//    val treeFlow : Flow<ILog> = mapAsFlow.map {
//        it.entries.fold(ILog.LogBlock(EmptySerializedTag(), mapOf()))
//    }
//
//    val treeFlowDiffSource = MutableStateFlow()
//
//    fun treeDiffFlow(reference: ILog): Flow<TreeDiff>{
//
//    }
//    interface NodeId{
//
//    }
//    interface NodeDescription{
//
//    }
//    interface ConnectionDirection{
//
//    }
//    interface ConnectionInformation{}
//    interface TemporalIndicator{}
//
//    sealed class TreeNodeDiff(open val nodeId: NodeId){
//        //abstract val innerDiffs: List<TreeDiff>
//        data class AddConnection(val parentId: List<NodeId>,
//                             override val nodeId: NodeId): TreeNodeDiff(nodeId){
//
//        }
//        data class NodeUpdate(val type: Class<ILog>, )
//        data class NodeInsert(override val nodeId: NodeId)
//        data class Root(open )
//        data class ChangeNodeDescription(
//            val connectionDescription: NodeDescription
//        )
//
//        sealed class SingleChange(val time: TemporalIndicator){
//            data class CreateNode()
//            data class CreateGroup()
//            data class UpdateDescription()
//
//        }
//        data class ChangeList(val changes: )
//
//    }
//
//
    override fun emit(log: Log) {
        logScope.launch{
            val serializedLog = logSerializer.serialize(log)
            val newList = listAsFlow.value.toMutableList() + serializedLog
            listAsFlow.value = newList
        }
    }


//
//
//    sealed class ILog(open val tag: SerializedTag<*>){
//        abstract val innerLogs: List<SerializedTag<*>>
//        data class LogBlock(override val tag: SerializedTag<*>, val map: Map<SerializedTag<*>, ILog>): ILog(tag){
//            override val innerLogs = map.values.flatMap { it.innerLogs }
//        }
//        data class LogStack(override val tag: SerializedTag<*>, val logs: List<ILog>) : ILog(tag){
//            override val innerLogs = logs.flatMap { it.innerLogs }
//        }
//        data class SingleLog(override val tag: SerializedTag<*>, val log: LogState) : ILog(tag){
//            override val innerLogs = listOf(tag)
//        }
//    }
//    data class Group(open val tag: SerializedTag<*>,val map: Map<SerializedTag<*>, ILog>)
//
//
//
//
//
    data class LogState(val params: List<EntityRepresentation<*>>,
                        val tagId: IdentityRepresentation,
                        val isActive : Boolean,
                        val isSuccess: Boolean,
                        val isErrored: Boolean,
                        val result: EntityRepresentation<*>?,
                        val error: EntityRepresentation<Throwable>?
    )
//    data class ReceiverState(
//        val receiver: ReceiverRepresentation,
//        val logs: List<LogState>
//    )
//
//
//
//    data class LogGroup<Key>(val groupId: Key, val name: String, val logs: List<LogState>)
//    //data class LogTree()
//    //data class TreeDiff()
//
////
////    data class BestLogRepresentation(
////        val receiver: ReceiverRepresentation,
////        val log
////    )
//

}