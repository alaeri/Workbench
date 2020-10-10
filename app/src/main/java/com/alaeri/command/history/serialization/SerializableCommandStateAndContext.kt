package com.alaeri.command.history.serialization

data class SerializableCommandStateAndContext<Key>(
    val context: SerializableCommandContext<Key>,
    val state: SerializableCommandState<Key>
)