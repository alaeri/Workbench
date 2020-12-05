package com.alaeri.command.graph

import com.alaeri.command.CommandNomenclature
import com.alaeri.command.history.IdOwner
import com.alaeri.command.history.id.IndexAndUUID
import com.alaeri.command.history.serialization.SerializableCommandStateAndContext
import com.alaeri.command.history.serialization.SerializedClass

object ComponentsGraphMapper : ISerializedCommandToGraphLevelsMapper{
    override fun buildLevels(filteredList: List<SerializableCommandStateAndContext<IndexAndUUID>>): Levels {
        val connections = filteredList.flatMap {
            val childContextElement = it.context.executionContext.toElement()
            val parentContextElement = it.context.invokationContext.toElement()
            val contextConnection = ElementConnection(
                child = childContextElement,
                parent = parentContextElement
            )
            val idOwnerState = it.state as? IdOwner<IndexAndUUID>
            val resultElement = idOwnerState?.toElement() as? Element<IndexAndUUID>
            val stateConnections = resultElement?.let {
                listOf(
                    ElementConnection<IndexAndUUID>(
                        child = it,
                        parent = childContextElement
                    )
                )
                //                if (childContextElement == parentContextElement) {
                //
                //                } else {
                //                    listOf(
                //                        ElementConnection<IndexAndUUID>(
                //                            child = it,
                //                            parent = parentContextElement
                //                        ),
                //                        ElementConnection<IndexAndUUID>(
                //                            child = childContextElement,
                //                            parent = it
                //                        )
                //                    )
                //                }
            } ?: emptyList<ElementConnection<IndexAndUUID>>()
            return@flatMap stateConnections + contextConnection
        }
        val distinctElements = connections.flatMap {
            listOf(it.child, it.parent)
        }.distinct()
        val levels = mutableListOf<List<ElementAndParents<IndexAndUUID>>>()
        val cleanedConnections = connections.filter { it.child != it.parent }.distinct()
        val connectionsGroupedByChild = cleanedConnections.groupBy { it.child }
        val connectionsGroupedByParent = cleanedConnections.groupBy { it.parent }
        val previousLevelElements = listOf<Element<IndexAndUUID>>()
        var remainingElements = distinctElements
        while (remainingElements.isNotEmpty()) {
            val nextElements = remainingElements.filter {
                val parents = connectionsGroupedByChild.get(it)?.map { it.parent }
                //Log.d("CATS", "it: $it parents: ${parents}")
                parents?.none { it in remainingElements } ?: true
            }.map {
                ElementAndParents(it, connectionsGroupedByChild[it]?.map { it.parent } ?: listOf())
            }
            if (nextElements.isEmpty()) {
                //Log.d("CATS", "chain is broken...")
                break
            }
            levels += nextElements
            remainingElements =
                remainingElements.filterNot { it in nextElements.map { it.element } }
//            Log.d(
//                "CATS",
//                "next: ${nextElements.size} remaining: ${remainingElements.size} depth: ${levels.size}"
//            )
        }
        val levelsToJson = Levels(levels.map { level ->
            level.map { elementAndParents ->
                IdAndParents(
                    elementAndParents.element.toStr(),
                    elementAndParents.parents.map { it.toStr() })
            }
        })
        return levelsToJson
    }
}
sealed class GraphElement(open val key: IndexAndUUID){
    data class Receiver(override val key:IndexAndUUID, val serializedClass: SerializedClass): GraphElement(key)
    data class Command(override val key:IndexAndUUID, val commandNomenclature: CommandNomenclature, val name: String?): GraphElement(key)
    data class State(override val key: IndexAndUUID, val serializedClass: SerializedClass?): GraphElement(key)

    fun toStr() : String= when(this){
        is Receiver -> "${key.index} $serializedClass"
        is Command -> "${key.index} ${if(commandNomenclature != CommandNomenclature.Undefined){ commandNomenclature.javaClass.simpleName}else {""}} ${name ?: ""}"
        is State -> "${key.index}" + if(this.serializedClass!= null){" $serializedClass" }else{""}
    }
}
sealed class ParentKey()
object CommandsGraphMapper: ISerializedCommandToGraphLevelsMapper{
    override fun buildLevels(list: List<SerializableCommandStateAndContext<IndexAndUUID>>): Levels {
        val entries: List<Pair<GraphElement, List<IndexAndUUID>>> =
            list.flatMap { serialCommStateCont ->
                val isRoot = serialCommStateCont.context.commandNomenclature == CommandNomenclature.Root
                val commandGraphElement = GraphElement.Command(
                    serialCommStateCont.context.commandId,
                    serialCommStateCont.context.commandNomenclature,
                    serialCommStateCont.context.commandName
                )
                val receiverGraphElement = GraphElement.Receiver(
                    serialCommStateCont.context.executionContext.id,
                    serialCommStateCont.context.executionContext.serializedClass
                )
                val invokerKey = if(!isRoot) {
                    serialCommStateCont.context.invokationContext.id
                }else{
                    null
                }
                val parentCommandKey = if(!isRoot){
                    serialCommStateCont.context.invokationCommandId
                }else{
                    null
                }
                val receiverKey = serialCommStateCont.context.executionContext.id
                val commandKey = serialCommStateCont.context.commandId

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
            println(it.key)
            println(it.value)
        }
        return Levels(levels.map { level -> level.map { IdAndParents(it.node.toStr(), it.parents.map { it.toStr() }) } })
    }

}