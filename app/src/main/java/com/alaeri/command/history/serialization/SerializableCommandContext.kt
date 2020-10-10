package com.alaeri.command.history.serialization

import com.alaeri.command.android.CommandNomenclature

data class SerializableCommandContext<Key>(
    val commandId: Key,
    val invokationCommandId: Key?,
    val invokationContext: SerializableInvokationContext<Key>,
    val executionContext: SerializableInvokationContext<Key>,
    val depth: Int,
    val commandName: String?,
    val commandNomenclature: CommandNomenclature
){
    override fun toString(): String {
        return "$commandName-$commandNomenclature, ${commandId.toString()} invokation:${invokationContext} execution:${executionContext}"
    }
}