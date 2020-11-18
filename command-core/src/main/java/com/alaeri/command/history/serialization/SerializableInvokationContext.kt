package com.alaeri.command.history.serialization

import com.alaeri.command.history.toSerializedClass

data class SerializableInvokationContext<Key>(
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