package com.alaeri.command.graph.command

import com.alaeri.command.CommandNomenclature
import com.alaeri.command.graph.ISerializedCommandToGraphLevelsMapper
import com.alaeri.command.graph.GraphNode
import com.alaeri.command.graph.GraphRepresentation
import com.alaeri.command.graph.serializedUnit
import com.alaeri.command.serialization.IdOwner
import com.alaeri.command.serialization.id.IndexAndUUID
import com.alaeri.command.serialization.entity.SerializableCommandStateAndScope

object CommandsGraphMapper: ISerializedCommandToGraphLevelsMapper {
    override fun buildGraph(list: List<SerializableCommandStateAndScope<IndexAndUUID>>): GraphRepresentation {
        val entries: List<Pair<GraphElement, List<IndexAndUUID>>> =
            list.flatMap { serialCommStateCont ->
                val isRoot = serialCommStateCont.scope.commandNomenclature == CommandNomenclature.Root
                val commandGraphElement = GraphElement.Command(
                    serialCommStateCont.scope.commandId,
                    serialCommStateCont.scope.commandNomenclature,
                    serialCommStateCont.scope.commandName
                )
                val receiverGraphElement = GraphElement.Receiver(
                    serialCommStateCont.scope.commandExecutionScope.id,
                    serialCommStateCont.scope.commandExecutionScope.serializedClass
                )
                val invokerKey = if(!isRoot) {
                    serialCommStateCont.scope.commandInvokationScope.id
                }else{
                    null
                }
                val parentCommandKey = if(!isRoot){
                    serialCommStateCont.scope.invokationCommandId
                }else{
                    null
                }
                val receiverKey = serialCommStateCont.scope.commandExecutionScope.id
                val commandKey = serialCommStateCont.scope.commandId

                val idOwnerState = serialCommStateCont.state as? IdOwner<IndexAndUUID>
                val stateGraphElement = idOwnerState?.let {
                    val id = it.id
                    if (id != null) {
                        GraphElement.State(id, it.clazz)
                    } else null
                }

                val receiverAndParents =
                    receiverGraphElement to listOfNotNull(invokerKey)
                val commandAndParents =
                    commandGraphElement to listOfNotNull(invokerKey, parentCommandKey, receiverKey)
                val stateAndParents = if (stateGraphElement != null) {
                    null//stateGraphElement to listOf<IndexAndUUID>() //commandKey, executionKey
                } else {
                    null
                }
                listOf( commandAndParents, stateAndParents).filterNotNull()
            }
        val map: Map<IndexAndUUID, List<Pair<GraphElement, List<IndexAndUUID>>>> = entries.groupBy {
            it.first.key
        }
        val flatMap: Map<GraphElement, List<IndexAndUUID>> = map.mapKeys { it.value.first().first }.mapValues { it.value.flatMap{ it.second }.filterNotNull() }
        val cleandedMap = flatMap
            .filterKeys { it !is GraphElement.State || it.serializedClass != serializedUnit }
            .mapValues {
                val elementKey = it.key.key
                it.value.filter { it != elementKey }
            }.mapValues {
                it.value.filter{ parent -> flatMap.any { it.key.key == parent } }
            }
        var remainingItemsMap = cleandedMap



        val existingRoots = mutableListOf<IndexAndUUID>()
        val existingParents = mutableListOf<GraphElement>()
        data class NodeAndParents(val node: GraphElement, val parents: List<GraphElement>)
        val levels = mutableListOf<List<NodeAndParents>>()




        while (remainingItemsMap.isNotEmpty()){
            val levelEntries = remainingItemsMap.filterValues { parents -> parents.all { parent -> existingRoots.contains(parent) } }
            val level = levelEntries.map { NodeAndParents(it.key, it.value.map { parentKey -> existingParents.first { existingParent -> existingParent.key ==  parentKey } }) }
            levels += level
            existingRoots += levelEntries.keys.map { it.key }
            existingParents += level.map { it.node }
            remainingItemsMap = remainingItemsMap.filter { !existingRoots.contains(it.key.key) }
            if(levelEntries.isEmpty()){
                //println("remainingItems: ${remainingItemsMap.size}")
                break
            }else{
                //println("items: ${levelEntries.size} remainingItems: ${remainingItemsMap.size}")
            }
        }
        remainingItemsMap.entries.take(3).forEach {
//            println(it.key)
//            println(it.value)
        }
        return GraphRepresentation(levels.map { level ->
            level.map {
                GraphNode(
                    it.node.toStr(),
                    it.parents.map { it.toStr() })
            }
        })
    }

}