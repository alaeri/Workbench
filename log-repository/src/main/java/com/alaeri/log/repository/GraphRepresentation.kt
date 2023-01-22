package com.alaeri.log.repository

sealed class HistoryItem{

    enum class ActorType{
        Receiver,
        Log
    }
    data class Actor(val id: GroupKey,
                     val name: String,
                     val actorType: ActorType) : HistoryItem()

    enum class LineType{
        FromReceiver,
        FromParentStart,
        ToParentEnd,
        OnEach
    }


    data class Line(val from: GroupKey,
                    val to: GroupKey,
                    val name: String,
                    val lineType: LineType,
                    val index: Int = 1,
                    val isActive: Boolean = false
                    ): HistoryItem()

    data class Receiver(
        val name: String,
        val contained: List<GroupKey>
    ): HistoryItem()
}



data class GraphRepresentation(val items: List<HistoryItem>, val lastIndex: Int = 1)