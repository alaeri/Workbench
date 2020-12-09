package com.alaeri.command.serialization.entity

data class SerializableCommandStateAndScope<Key>(
    val scope: SerializableCommandScope<Key>,
    val state: SerializableCommandState<Key>,
    val time: Long
)