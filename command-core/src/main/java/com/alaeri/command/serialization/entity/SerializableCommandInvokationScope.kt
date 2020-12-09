package com.alaeri.command.serialization.entity

import com.alaeri.command.serialization.toSerializedClass

data class SerializableCommandInvokationScope<Key>(
    val id: Key,
    val serializedClass: SerializedClass,
    val parentExecutionId: String?,
    val coroutineContextId: String?,
    val invokationThreadId: String?){
    override fun toString(): String {
        return when(serializedClass){
            Unit::class.toSerializedClass() -> "Unit"
            else ->  "$id-${serializedClass.toString()}"
        }

    }
}