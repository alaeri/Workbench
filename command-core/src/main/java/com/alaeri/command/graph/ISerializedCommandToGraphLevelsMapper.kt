package com.alaeri.command.graph

import com.alaeri.command.history.id.IndexAndUUID
import com.alaeri.command.history.serialization.SerializableCommandStateAndContext

interface ISerializedCommandToGraphLevelsMapper{
    fun buildLevels(list: List<SerializableCommandStateAndContext<IndexAndUUID>>): Levels
}