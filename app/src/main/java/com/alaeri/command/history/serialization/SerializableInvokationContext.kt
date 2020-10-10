package com.alaeri.command.history.serialization

data class SerializableInvokationContext<Key>(
    val id: Key,
    val serializedClass: SerializedClass,
    val parentExecutionId: String?,
    val coroutineContextId: String?,
    val invokationThreadId: String?){
    override fun toString(): String {
        return "$id-${serializedClass.toString()}"
    }
}