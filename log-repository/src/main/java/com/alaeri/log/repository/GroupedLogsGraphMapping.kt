package com.alaeri.log.repository

import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.extra.tag.name.NameRepresentation
import com.alaeri.log.extra.tag.receiver.ReceiverRepresentation
import com.alaeri.log.serialize.serialize.SerializedLog
import com.alaeri.log.serialize.serialize.representation.FiliationRepresentation
import com.alaeri.log.serialize.serialize.representation.ListRepresentation

/**
 * Created by Emmanuel Requier on 30/12/2020.
 */
object GroupedLogsGraphMapper {

    fun mapToGraph(list: List<SerializedLog<IdentityRepresentation>>): GraphRepresentation {
        val entries: List<Pair<LogGroupId, CommandInvokation>> =
            list.map { log ->
                val listTag = log.tag as ListRepresentation
                val receiver = listTag.representations.filterIsInstance<ReceiverRepresentation>().first()
                val name = listTag.representations.filterIsInstance<NameRepresentation>().first()
                val parent = listTag.representations.filterIsInstance<FiliationRepresentation>().firstOrNull()
                val commandGroupIdentifier = LogGroupId(name.name, receiver.identity, receiver.type.clazz)
                commandGroupIdentifier to CommandInvokation(listTag.identity, listOfNotNull<IdentityRepresentation>(
                    if(parent != null){
                        parent.parentRepresentation.identity
                    }else{
                        null
                    })
                )
            }
        val map: Map<LogGroupId, List<Pair<LogGroupId, CommandInvokation>>> = entries.groupBy {
            it.first
        }
        val flatMap: Map<LogGroupId, List<CommandInvokation>> = map.mapValues { it.value.map{ it.second } }
        val invertedMap: Map<IdentityRepresentation, List<LogGroupId>> = flatMap.entries.flatMap { entry ->
            val commandInvokations: List<CommandInvokation> = entry.value
            commandInvokations.map {
                it.tagId to entry.key
            }
        }.groupBy { it.first }.mapValues {
            it.value.map { it.second  }
        }
        data class InvokationGroup(val commandId: IdentityRepresentation, val relations: List<LogGroupId>)

        val groupedMap : Map<LogGroupId, List<InvokationGroup>> = flatMap.mapValues { entry ->
            entry.value.map {
                val groupRelations: List<LogGroupId> = it.relations.flatMap { foreignKey ->
                    invertedMap[foreignKey] ?: listOf()
                }.filter {
                    it != entry.key
                }
                InvokationGroup(it.tagId, groupRelations)
            }
        }

        var remainingItemsMap = groupedMap.mapValues { it.value.flatMap { it.relations } }
        val existingRoots = mutableListOf<LogGroupId>()

        val existingParents = mutableListOf<LogGroupId>()
        data class NodeAndParents(val node: LogGroupId, val parents: List<LogGroupId>, val invokationCount: Int)
        val levels = mutableListOf<List<NodeAndParents>>()

        while (remainingItemsMap.isNotEmpty()){
            val levelEntries = remainingItemsMap.filterValues { parents -> parents.all { parent -> existingRoots.contains(parent) } }
            val level = levelEntries.map { entry ->
                val parents = entry.value
                NodeAndParents(entry.key, parents.distinct(), parents.size)
            }
            levels += level
            existingRoots += levelEntries.keys
            existingParents += level.map { it.node }
            remainingItemsMap = remainingItemsMap.filter { !existingRoots.contains(it.key) }
        }

        val keys = groupedMap.keys
        return GraphRepresentation(levels.map { level ->
            level.map {
                GraphNode(
                    keys.indexOf(it.node).toString(),
                    it.parents.map { keys.indexOf(it).toString() },
                    "${it.node.receiverClass.simpleName} - ${it.node.name} - (${it.invokationCount})"
                )
            }
        })
    }

}
