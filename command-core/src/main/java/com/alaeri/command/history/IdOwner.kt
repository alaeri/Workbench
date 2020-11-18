package com.alaeri.command.history

import com.alaeri.command.history.serialization.SerializedClass

interface IdOwner<Key>{
    val id: Key?
    val clazz: SerializedClass?
}