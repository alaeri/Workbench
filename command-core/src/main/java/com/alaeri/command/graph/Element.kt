package com.alaeri.command.graph

import com.alaeri.command.history.serialization.SerializedClass

data class Element<Key>(val id: Key, val clazz: SerializedClass)