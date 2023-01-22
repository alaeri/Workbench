package com.alaeri.log.repository

import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.extra.tag.name.NameRepresentation
import com.alaeri.log.extra.tag.receiver.ReceiverRepresentation
import com.alaeri.log.serialize.serialize.SerializedLog
import com.alaeri.log.serialize.serialize.SerializedLogMessage
import com.alaeri.log.serialize.serialize.representation.FiliationRepresentation
import com.alaeri.log.serialize.serialize.representation.ListRepresentation
import com.alaeri.log.serialize.serialize.representation.ParentRepresentation

/**
 * Created by Emmanuel Requier on 30/12/2020.
 */
object StableGraphMapper {


    data class LineKey(val from: GroupKey,
                       val to: GroupKey,
                       val messageType: Class<SerializedLogMessage>
    )
    data class LineOccurrences(
        val lastOccurrenceIndex: Int,
        val occurrences: List<SerializedLogMessage>,
        var isActivated: Boolean,
    )
    data class Actor(val nameRepresentation: NameRepresentation,
                     val receiverRepresentation: ReceiverRepresentation)
    data class CurrentGraph(
        val actors : MutableMap<GroupKey, Actor>,
        val lines: MutableMap<LineKey, LineOccurrences>,
        var lastIndex: Int,
    )

    fun SerializedLogMessage.toSpecificString(): String{
        return when(val message = this){
            is SerializedLogMessage.OnEach -> message.item?.toString()
            is SerializedLogMessage.Start -> message.parameters?.toString()
            is SerializedLogMessage.Success -> message.entityRepresentation?.toString()
            is SerializedLogMessage.Error -> message.throwableRepresentation?.toString()
        } ?: ""
    }
    fun SerializedLogMessage.toLineType() = when(this){
        is SerializedLogMessage.Error -> HistoryItem.LineType.ToParentEnd
        is SerializedLogMessage.Start -> HistoryItem.LineType.FromParentStart
        is SerializedLogMessage.Success ->HistoryItem.LineType.ToParentEnd
        is SerializedLogMessage.OnEach -> HistoryItem.LineType.OnEach
    }



    enum class GroupField{
        Identity,
        Type,
        Name,
        CallSite
    }




    fun mapToGraph(list: List<SerializedLog<IdentityRepresentation>>): GraphRepresentation {
        val currentGraph = CurrentGraph(mutableMapOf(), mutableMapOf(), 0)
        fun deactivateMatchingStartMessage(lineKey: LineKey, message: SerializedLogMessage){
            if(message is SerializedLogMessage.Error || message is SerializedLogMessage.Success){
                val startLK = lineKey.copy(messageType = SerializedLogMessage.Start(
                    emptyList()
                ).javaClass)
                currentGraph.lines[startLK]?.isActivated = false
            }
        }
        list.forEachIndexed { index,  log ->
            val listTag = log.tag as ListRepresentation
            val message = log.message as SerializedLogMessage
            val name = listTag.representations.filterIsInstance<NameRepresentation>().first()
            val receiver = listTag.representations.filterIsInstance<ReceiverRepresentation>().first()
            val groupKey = GroupKey(receiver.type.clazz, name.name)


           if(currentGraph.actors[groupKey] == null){
                currentGraph.actors[groupKey] = Actor(name, receiver)
            }

            println("parentsAndChildren for $groupKey")
            val parents = listTag.representations.filterIsInstance<FiliationRepresentation>()
            parents.forEach { filiationRepresentation ->
                val parentListRep = filiationRepresentation.parentRepresentation as ListRepresentation
                val parentReceiver = parentListRep.representations.filterIsInstance<ReceiverRepresentation>().first()
                val parentName = parentListRep.representations.filterIsInstance<NameRepresentation>().first()
                val parentGroupKey = GroupKey(parentReceiver.type.clazz, parentName.name)
                println("parentGroupKey : $parentGroupKey groupKey: $groupKey")
                if(currentGraph.actors[parentGroupKey] == null){
                    currentGraph.actors[parentGroupKey] = Actor(parentName, receiver)
                }
                val lineKey = LineKey(
                    from = parentGroupKey,
                    to = groupKey,
                    messageType = message.javaClass)
                val currentLineOccurrences = currentGraph.lines[lineKey]
                currentGraph.lines[lineKey] = currentLineOccurrences?.copy(
                    lastOccurrenceIndex = index,
                    occurrences = currentLineOccurrences.occurrences + message,
                    isActivated = true
                ) ?: LineOccurrences(
                    lastOccurrenceIndex = index,
                    occurrences = listOf(message),
                    isActivated = true
                )
                deactivateMatchingStartMessage(lineKey, message)
            }


            val children = listTag.representations.filterIsInstance<ParentRepresentation>()
            val childrenLines = children.forEach { parentRep ->
                val childListRep = parentRep.childRep as ListRepresentation
                val childReceiver = childListRep.representations.filterIsInstance<ReceiverRepresentation>().first()
                val childName = childListRep.representations.filterIsInstance<NameRepresentation>().first()
                val childGroupKey = GroupKey(childReceiver.type.clazz, childName.name)
                println("childGroupKey : $childGroupKey groupKey: $groupKey")
                if(currentGraph.actors[childGroupKey] == null){
                    currentGraph.actors[childGroupKey] = Actor(childName, receiver)
                }
                val lineKey = LineKey(
                    from = groupKey,
                    to = childGroupKey,
                    messageType = message.javaClass)
                val currentLineOccurrences = currentGraph.lines[lineKey]
                currentGraph.lines[lineKey] = currentLineOccurrences?.copy(
                    lastOccurrenceIndex = index,
                    occurrences = currentLineOccurrences.occurrences + message,
                    isActivated = true
                ) ?: LineOccurrences(
                    lastOccurrenceIndex = index,
                    occurrences = listOf(message),
                    isActivated = true
                )
                deactivateMatchingStartMessage(lineKey, message)
            }
            currentGraph.lastIndex = index
        }
        val receivers: List<HistoryItem.Receiver> = currentGraph.actors.entries.groupBy{it.value.receiverRepresentation.type.clazz.simpleName}.entries.map {
            HistoryItem.Receiver(it.key, it.value.map { inner -> inner.key } )
        }
        val actorsByName =  currentGraph.actors.entries
            .sortedBy {
                val name = it.value.nameRepresentation.name
                when{
                    name.endsWith("-outer") -> -2
                    name.endsWith("-inner") -> -1
                    else -> 0
                }
            }
        println("BEFORE SORT")
        actorsByName.forEach { println(it) }
        val actorsByReceiver = actorsByName.sortedBy { val receiverName = it.key.type.simpleName
                when {
                    receiverName.endsWith("App") -> -3
                    receiverName.endsWith("ViewModel") -> -2
                    receiverName.endsWith("Repository") -> -1
                    else -> 0
                }
            }
        println("AFTER SORT")
        val actors = actorsByReceiver.map {
                HistoryItem.Actor(it.key, it.value.nameRepresentation.name, HistoryItem.ActorType.Log)
            }
        val items = actors + receivers + currentGraph.lines.map {
            HistoryItem.Line(it.key.from, it.key.to,
                it.value.occurrences.last().toSpecificString(),
                it.value.occurrences.last().toLineType(),
                index = it.value.lastOccurrenceIndex,
                isActive = it.value.isActivated && it.key.messageType == SerializedLogMessage.Start::class.java
            )
        }
        println("ACTOOOORRS")
        items.filterIsInstance<HistoryItem.Actor>().forEach { println(it) }
        return GraphRepresentation(items, lastIndex = currentGraph.lastIndex)
    }

}
