package com.alaeri.command.graph.group

import com.alaeri.command.graph.ISerializedCommandToGraphLevelsMapper
import com.alaeri.command.graph.IdAndParents
import com.alaeri.command.graph.Levels
import com.alaeri.command.graph.serializedUnit
import com.alaeri.command.history.id.IndexAndUUID
import com.alaeri.command.history.serialization.SerializableCommandStateAndContext

object GroupedCommandsGraphMapper: ISerializedCommandToGraphLevelsMapper {
    override fun buildLevels(list: List<SerializableCommandStateAndContext<IndexAndUUID>>): Levels {
        val entries: List<Pair<CommandGroupIdentifier, CommandInvokation>> =
            list.map { serialCommStateCont ->
                 val commandGroupIdentifier = CommandGroupIdentifier(
                     serialCommStateCont.context.commandNomenclature,
                     serialCommStateCont.context.commandName,
                     serialCommStateCont.context.executionContext
                 )
                commandGroupIdentifier to CommandInvokation(serialCommStateCont.context.commandId, listOfNotNull<IndexAndUUID>(
                    if(serialCommStateCont.context.commandId!= serialCommStateCont.context.invokationCommandId){
                        serialCommStateCont.context.invokationCommandId
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
        data class NodeAndParents(val node: CommandGroupIdentifier, val parents: List<CommandGroupIdentifier>)
        val levels = mutableListOf<List<NodeAndParents>>()

//        println("starting items: ")
//        println(remainingItemsMap)
        while (remainingItemsMap.isNotEmpty()){
            val levelEntries = remainingItemsMap.filterValues { parents -> parents.all { parent -> existingRoots.contains(parent) } }
            val level = levelEntries.map { entry ->
                val parents = entry.value
                NodeAndParents(entry.key, parents)
            }
            levels += level
            existingRoots += levelEntries.keys
            existingParents += level.map { it.node }
            remainingItemsMap = remainingItemsMap.filter { !existingRoots.contains(it.key) }
            if(levelEntries.isEmpty()){
                //println("EMPTY LEVEL!!! remainingItems: ${remainingItemsMap.size}")
                break
            }else{
                //println("items: ${levelEntries.size} remainingItems: ${remainingItemsMap.size}")
            }
        }
//        remainingItemsMap.entries.take(3).forEach {
//            println(it.key)
//            println(it.value)
//        }
        val keys = groupedMap.keys
        return Levels(levels.map { level ->
            level.map {
                IdAndParents(
                    keys.indexOf(it.node).toString(),
                    it.parents.map { keys.indexOf(it).toString() },
                    "${keys.indexOf(it.node).toString()} - ${it.node.receiverContext.serializedClass.simpleName} - ${it.node.name ?: ""}"
                )
            }
        })
    }

}