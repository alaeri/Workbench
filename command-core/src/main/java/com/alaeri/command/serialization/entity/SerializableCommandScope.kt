package com.alaeri.command.serialization.entity

import com.alaeri.command.CommandNomenclature

data class SerializableCommandScope<Key>(
    val commandId: Key,
    val invokationCommandId: Key,
    val commandInvokationScope: SerializableCommandInvokationScope<Key>,
    val commandExecutionScope: SerializableCommandInvokationScope<Key>,
    val depth: Int,
    val commandName: String?,
    val commandNomenclature: CommandNomenclature
){
    override fun toString(): String {
        return "$commandName-$commandNomenclature, ${commandId.toString()} invokation:${commandInvokationScope} execution:${commandExecutionScope}"
    }
}