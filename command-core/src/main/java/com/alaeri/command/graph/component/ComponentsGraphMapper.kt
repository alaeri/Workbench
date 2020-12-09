package com.alaeri.command.graph

import com.alaeri.command.graph.component.Element
import com.alaeri.command.graph.component.ElementAndParents
import com.alaeri.command.graph.component.ElementConnection
import com.alaeri.command.serialization.IdOwner
import com.alaeri.command.serialization.id.IndexAndUUID
import com.alaeri.command.serialization.entity.SerializableCommandStateAndScope
import toElement
import toStr

object ComponentsGraphMapper : ISerializedCommandToGraphLevelsMapper{
    override fun buildGraph(filteredList: List<SerializableCommandStateAndScope<IndexAndUUID>>): GraphRepresentation {
        val connections = filteredList.flatMap {
            val childContextElement = it.scope.commandExecutionScope.toElement()
            val parentContextElement = it.scope.commandInvokationScope.toElement()
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
                //                         ElementConnection<IndexAndUUID>(
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
        val levelsToJson = GraphRepresentation(levels.map { level ->
            level.map { elementAndParents ->
                GraphNode(
                    elementAndParents.element.toStr(),
                    elementAndParents.parents.map { it.toStr() })
            }
        })
        return levelsToJson
    }
}
