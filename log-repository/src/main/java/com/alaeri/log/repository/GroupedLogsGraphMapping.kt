package com.alaeri.log.repository

import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.extra.tag.name.NameRepresentation
import com.alaeri.log.extra.tag.receiver.ReceiverRepresentation
import com.alaeri.log.serialize.serialize.Representation
import com.alaeri.log.serialize.serialize.SerializedLog
import com.alaeri.log.serialize.serialize.SerializedLogMessage
import com.alaeri.log.serialize.serialize.representation.FiliationRepresentation
import com.alaeri.log.serialize.serialize.representation.ListRepresentation
import com.alaeri.log.serialize.serialize.representation.ParentRepresentation

/**
 * Created by Emmanuel Requier on 30/12/2020.
 */
object ChronologicalGraphMapper {








    enum class GroupField{
        Identity,
        Type,
        Name,
        CallSite
    }

    fun SerializedLogMessage.toSpecificString(): String{
        return when(val message = this){
            is SerializedLogMessage.OnEach -> message.item?.toString()
            is SerializedLogMessage.Start -> message.parameters?.toString()
            is SerializedLogMessage.Success -> message.entityRepresentation?.toString()
            is SerializedLogMessage.Error -> message.throwableRepresentation?.toString()
        } ?: ""
    }

    fun mapToGraph(list: List<SerializedLog<IdentityRepresentation>>): GraphRepresentation {
        val knownParticipants = mutableMapOf<GroupKey, Representation<*>>()
        val items : List<HistoryItem> = list.flatMap { log ->
            val listTag = log.tag as ListRepresentation
            val message = log.message as SerializedLogMessage
            val name = listTag.representations.filterIsInstance<NameRepresentation>().first()
            val receiver = listTag.representations.filterIsInstance<ReceiverRepresentation>().first()
            val groupKey = GroupKey(receiver.type.clazz, name.name)
            val receiverKey = GroupKey(receiver.type.clazz, null)
            val receivers = if(knownParticipants[receiverKey] == null){
                knownParticipants[receiverKey] = receiver
                listOf(
                    HistoryItem.Actor(receiverKey, receiver.type.clazz.simpleName, HistoryItem.ActorType.Receiver))
            }else{
                emptyList()
            }
            val nameActorAndLines = if(knownParticipants[groupKey] == null){
                knownParticipants[groupKey] = name
                listOf(
                    HistoryItem.Actor(groupKey, name.name, HistoryItem.ActorType.Log),
                    HistoryItem.Line(from = receiverKey,
                        to = groupKey,
                        name = ".....",
                        HistoryItem.LineType.FromReceiver)
                )
            }else{
                emptyList()
            }
            val lineType = when(message){
                is SerializedLogMessage.Error -> HistoryItem.LineType.ToParentEnd
                is SerializedLogMessage.Start -> HistoryItem.LineType.FromParentStart
                is SerializedLogMessage.Success ->HistoryItem.LineType.ToParentEnd
                is SerializedLogMessage.OnEach -> HistoryItem.LineType.OnEach
            }

            println("parentsAndChildren for $groupKey")
            val parents = listTag.representations.filterIsInstance<FiliationRepresentation>()
            val parentLines = parents.flatMap { filiationRepresentation ->
                val parentListRep = filiationRepresentation.parentRepresentation as ListRepresentation
                val parentReceiver = parentListRep.representations.filterIsInstance<ReceiverRepresentation>().first()
                val parentName = parentListRep.representations.filterIsInstance<NameRepresentation>().first()
                val parentGroupKey = GroupKey(parentReceiver.type.clazz, parentName.name)
                println("parentGroupKey : $parentGroupKey groupKey: $groupKey")
                val actor = if(knownParticipants[parentGroupKey] == null){
                    knownParticipants[parentGroupKey] = parentName
                    HistoryItem.Actor(parentGroupKey, name.name, actorType = HistoryItem.ActorType.Log)
                }else{
                    null
                }
                listOfNotNull(
                    actor,
                    HistoryItem.Line(from = parentGroupKey, to = groupKey, name= message.toSpecificString(), lineType = lineType)
                )
            }


            val children = listTag.representations.filterIsInstance<ParentRepresentation>()
            val childrenLines = children.flatMap { parentRep ->
                val childListRep = parentRep.childRep as ListRepresentation
                val childReceiver = childListRep.representations.filterIsInstance<ReceiverRepresentation>().first()
                val childName = childListRep.representations.filterIsInstance<NameRepresentation>().first()
                val childGroupKey = GroupKey(childReceiver.type.clazz, childName.name)
                println("childGroupKey : $childGroupKey groupKey: $groupKey")
                val actor = if(knownParticipants[childGroupKey] == null){
                    knownParticipants[childGroupKey] = childName
                    HistoryItem.Actor(childGroupKey, name.name, actorType = HistoryItem.ActorType.Log)
                }else{
                    null
                }
                listOfNotNull(
                    actor,
                    HistoryItem.Line(from = groupKey, to = childGroupKey, name= message.toSpecificString(), lineType = lineType),
                )
            }
            receivers + nameActorAndLines  + childrenLines + parentLines

        }
        return GraphRepresentation(items)
    }

}
