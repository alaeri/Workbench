package com.alaeri.command.graph.group

import com.alaeri.command.graph.ISerializedCommandToGraphLevelsMapper
import com.alaeri.command.graph.GraphNode
import com.alaeri.command.graph.GraphRepresentation
import com.alaeri.command.serialization.id.IndexAndUUID
import com.alaeri.command.serialization.entity.SerializableCommandStateAndScope

object GroupedCommandsGraphMapper: ISerializedCommandToGraphLevelsMapper {
    override fun buildGraph(list: List<SerializableCommandStateAndScope<IndexAndUUID>>): GraphRepresentation {
        val entries: List<Pair<CommandGroupIdentifier, CommandInvokation>> =
            list.map { serialCommStateCont ->
                 val commandGroupIdentifier = CommandGroupIdentifier(
                     serialCommStateCont.scope.commandNomenclature,
                     serialCommStateCont.scope.commandName,
                     serialCommStateCont.scope.commandExecutionScope
                 )
                commandGroupIdentifier to CommandInvokation(serialCommStateCont.scope.commandId, listOfNotNull<IndexAndUUID>(
                    if(serialCommStateCont.scope.commandId!= serialCommStateCont.scope.invokationCommandId){
                        serialCommStateCont.scope.invokationCommandId
                    }else{
                        null
                    })
                )
            }
        val map: Map<CommandGroupIdentifier, List<Pair<CommandGroupIdentifier, CommandInvokation>>> = entries.groupBy {
            it.first
        }
        val flatMap: Map<CommandGroupIdentifier, List<CommandInvokation>> = map.mapValues { it.value.map{ it.second } }
        val invertedMap: Map<IndexAndUUID, List<CommandGroupIdentifier>> = flatMap.entries.flatMap { entry ->
            val commandInvokations: List<CommandInvokation> = entry.value
            commandInvokations.map {
                it.commandId to entry.key
            }
        }.groupBy { it.first }.mapValues {
            it.value.map { it.second  }
        }
        data class InvokationGroup(val commandId: IndexAndUUID, val relations: List<CommandGroupIdentifier>)

        val groupedMap : Map<CommandGroupIdentifier, List<InvokationGroup>> = flatMap.mapValues { entry ->
            entry.value.map {
                val groupRelations: List<CommandGroupIdentifier> = it.relations.flatMap { foreignKey ->
                    invertedMap[foreignKey] ?: listOf()
                }.filter {
                    it != entry.key
                }
                InvokationGroup(it.commandId, groupRelations)
            }
        }

        var remainingItemsMap = groupedMap.mapValues { it.value.flatMap { it.relations } }
        val existingRoots = mutableListOf<CommandGroupIdentifier>()

        val existingParents = mutableListOf<CommandGroupIdentifier>()
        data class NodeAndParents(val node: CommandGroupIdentifier, val parents: List<CommandGroupIdentifier>, val invokationCount: Int)
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
                    "${it.node.receiverContext.serializedClass.simpleName} - ${it.node.name ?: ""} - (${it.invokationCount})"
                )
            }
        })
    }

}