package com.alaeri.log.repository

import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.extra.tag.name.NameRepresentation
import com.alaeri.log.extra.tag.receiver.ReceiverRepresentation
import com.alaeri.log.serialize.serialize.Representation
import com.alaeri.log.serialize.serialize.SerializedLog
import com.alaeri.log.serialize.serialize.SerializedLogMessage
import com.alaeri.log.serialize.serialize.representation.FiliationRepresentation
import com.alaeri.log.serialize.serialize.representation.ListRepresentation

/**
 * Created by Emmanuel Requier on 30/12/2020.
 */
object GroupedLogsGraphMapper {

    fun mapToGraph(list: List<SerializedLog<IdentityRepresentation>>): GraphRepresentation {
        val knownParticipants = mutableMapOf<IdentityRepresentation, Representation<*>>()
        val items : List<HistoryItem> = list.flatMap { log ->
           val listTag = log.tag as ListRepresentation
            val message = log.message as SerializedLogMessage
            val name = listTag.representations.filterIsInstance<NameRepresentation>().first()
           val receiver = listTag.representations.filterIsInstance<ReceiverRepresentation>().first()
            val receivers = if(knownParticipants[receiver.identity] == null){
                knownParticipants[receiver.identity] = receiver
                listOf(HistoryItem.Actor(receiver.identity, receiver.type.clazz.simpleName))
            }else{
                emptyList()
            }
            val nameActorAndLines = if(knownParticipants[name.identity] == null){
                knownParticipants[name.identity] = name
                listOf(
                    HistoryItem.Actor(name.identity, name.name),
                    HistoryItem.Line(from = receiver.identity, to = name.identity, name = message.toString()))
            }else{
                emptyList()
            }
            knownParticipants[receiver.identity] = receiver
            val parents = listTag.representations.filterIsInstance<FiliationRepresentation>()
            val parentLines = parents.map { filiationRepresentation ->
                val parentListRep = filiationRepresentation.parentRepresentation as ListRepresentation
                val parentReceiver = parentListRep.representations.filterIsInstance<ReceiverRepresentation>().first()
                println(parentReceiver)
                val parentName = parentListRep.representations.filterIsInstance<NameRepresentation>().first()
                HistoryItem.Line(from = parentName.identity, to = name.identity, name= "...")
            }
           receivers + nameActorAndLines + parentLines

       }

//        val entries: List<Pair<LogGroupId, CommandInvokation>> =
//            list.map { log ->
//                val listTag = log.tag as ListRepresentation
//                val receiver = listTag.representations.filterIsInstance<ReceiverRepresentation>().first()
//                val name = listTag.representations.filterIsInstance<NameRepresentation>().first()
//                val parents = listTag.representations.filterIsInstance<FiliationRepresentation>()
//                if(parents.isEmpty()){
//                    //println("empty parents: $log")
//                }
//                val commandGroupIdentifier = LogGroupId(name.name, receiver.identity, receiver.type.clazz)
//                commandGroupIdentifier to CommandInvokation(listTag.identity, parents.map { it.parentRepresentation.identity })
//            }
//        val map: Map<LogGroupId, List<Pair<LogGroupId, CommandInvokation>>> = entries.groupBy {
//            it.first
//        }
//        val flatMap: Map<LogGroupId, List<CommandInvokation>> = map.mapValues { it.value.map{ it.second } }
//        val invertedMap: Map<IdentityRepresentation, List<LogGroupId>> = flatMap.entries.flatMap { entry ->
//            val commandInvokations: List<CommandInvokation> = entry.value
//            commandInvokations.map {
//                it.tagId to entry.key
//            }
//        }.groupBy { it.first }.mapValues {
//            it.value.map { it.second  }
//        }
//        data class InvokationGroup(val commandId: IdentityRepresentation, val relations: List<LogGroupId>)
//
//        val groupedMap : Map<LogGroupId, List<InvokationGroup>> = flatMap.mapValues { entry ->
//            entry.value.map {
//                val groupRelations: List<LogGroupId> = it.relations.flatMap { foreignKey ->
//                    invertedMap[foreignKey] ?: listOf()
//                }.filter {
//                    it != entry.key
//                }
//                InvokationGroup(it.tagId, groupRelations)
//            }
//        }
////        println("groupedMap")
////        groupedMap.entries.forEach {
////            println("key: ${it.key} - value: ${it.value}")
////        }
//
//        var remainingItemsMap = groupedMap.mapValues { it.value.flatMap { it.relations } }
//        val existingRoots = mutableListOf<LogGroupId>()
//
//        val existingParents = mutableListOf<LogGroupId>()
//        data class NodeAndParents(val node: LogGroupId, val parents: List<LogGroupId>, val invokationCount: Int)
//        val levels = mutableListOf<List<NodeAndParents>>()
//
//        while (remainingItemsMap.isNotEmpty()){
//            val levelEntries = remainingItemsMap.filterValues { parents -> parents.all { parent -> existingRoots.contains(parent) } }
//            val level = levelEntries.map { entry ->
//                val parents = entry.value
//                NodeAndParents(entry.key, parents.distinct(), parents.size)
//            }
//            levels += level
//            existingRoots += levelEntries.keys
//            existingParents += level.map { it.node }
//            remainingItemsMap = remainingItemsMap.filter { !existingRoots.contains(it.key) }
//        }
//
//        val keys = groupedMap.keys
//        return GraphRepresentation(levels.map { level ->
//            level.map {
//                GraphNode(
//                    keys.indexOf(it.node).toString(),
//                    it.parents.map { keys.indexOf(it).toString() },
//                    "${it.node.receiverClass.simpleName} - ${it.node.name} - (${it.invokationCount})"
//                )
//            }
//        })
        return GraphRepresentation(items.take(40))
    }

}
