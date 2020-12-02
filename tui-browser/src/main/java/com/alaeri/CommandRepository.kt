package com.alaeri

import com.alaeri.command.AbstractCommandLogger
import com.alaeri.command.history.ICommandRepository
import com.alaeri.command.history.id.IndexAndUUID
import com.alaeri.command.history.serialization.SerializableCommandStateAndContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class CommandRepository: ICommandRepository<IndexAndUUID>,
    AbstractCommandLogger<SerializableCommandStateAndContext<IndexAndUUID>> {

    val list = mutableListOf<SerializableCommandStateAndContext<IndexAndUUID>>()
    val sharedFlow = MutableStateFlow<List<SerializableCommandStateAndContext<IndexAndUUID>>>(list.toList())

    override val commands: Flow<List<SerializableCommandStateAndContext<IndexAndUUID>>> = sharedFlow
    override fun log(value: SerializableCommandStateAndContext<IndexAndUUID>) {
        list.add(value)
        sharedFlow.value = list.toList()
    }

    override fun clear() {
        list.clear()
        sharedFlow.value = list.toList()
    }
}