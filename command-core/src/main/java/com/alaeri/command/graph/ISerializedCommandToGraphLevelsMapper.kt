package com.alaeri.command.graph

import com.alaeri.command.serialization.id.IndexAndUUID
import com.alaeri.command.serialization.entity.SerializableCommandStateAndScope

interface ISerializedCommandToGraphLevelsMapper{
    fun buildGraph(list: List<SerializableCommandStateAndScope<IndexAndUUID>>): GraphRepresentation
}