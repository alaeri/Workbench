package com.alaeri.command.graph.component

import com.alaeri.command.serialization.entity.SerializedClass

data class Element<Key>(val id: Key, val clazz: SerializedClass)