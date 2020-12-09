package com.alaeri

import com.alaeri.command.GenericLogger
import com.alaeri.command.repository.ICommandRepository
import com.alaeri.command.serialization.id.IndexAndUUID
import com.alaeri.command.serialization.entity.SerializableCommandStateAndScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class CommandRepository: ICommandRepository<IndexAndUUID>,
    GenericLogger<SerializableCommandStateAndScope<IndexAndUUID>> {

    private val list = mutableListOf<SerializableCommandStateAndScope<IndexAndUUID>>()
    private val sharedFlow = MutableStateFlow<List<SerializableCommandStateAndScope<IndexAndUUID>>>(list.toList())

    override val commands: Flow<List<SerializableCommandStateAndScope<IndexAndUUID>>> = sharedFlow
    override fun log(value: SerializableCommandStateAndScope<IndexAndUUID>) {
        list.add(value)
        sharedFlow.value = list.toList()
    }

    override fun clear() {
        list.clear()
        sharedFlow.value = list.toList()
    }
}