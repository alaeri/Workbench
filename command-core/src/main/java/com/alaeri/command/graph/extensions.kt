package com.alaeri.command.graph

import com.alaeri.command.history.IdOwner
import com.alaeri.command.history.id.IndexAndUUID
import com.alaeri.command.history.serialization.SerializableInvokationContext
import com.alaeri.command.history.toSerializedClass

/**
 * Created by Emmanuel Requier on 28/11/2020.
 */
val serializedUnit = Unit.toSerializedClass()
fun <Key> SerializableInvokationContext<Key>.toElement() = Element(id, serializedClass)
fun <Key> IdOwner<Key>.toElement() : Element<Key>? = if(clazz != serializedUnit) { id?.let { id -> clazz?.let { Element(id, it) } } } else null
fun Element<IndexAndUUID>.toStr() = "$id $clazz"