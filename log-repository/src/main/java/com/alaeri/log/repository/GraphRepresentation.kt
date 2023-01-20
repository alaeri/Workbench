package com.alaeri.log.repository

import com.alaeri.log.extra.identity.IdentityRepresentation

sealed class HistoryItem{

    enum class ActorType{
        Receiver,
        Log
    }
    data class Actor(val id: GroupedLogsGraphMapper.GroupKey,
                     val name: String,
                     val actorType: ActorType) : HistoryItem()

    enum class LineType{
        FromReceiver,
        FromParentStart,
        ToParentEnd,
        OnEach
    }


    data class Line(val from: GroupedLogsGraphMapper.GroupKey,
                    val to: GroupedLogsGraphMapper.GroupKey,
                    val name: String,
                    val lineType: LineType,

                    ): HistoryItem()
}



data class GraphRepresentation(val items: List<HistoryItem>)