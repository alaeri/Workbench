package com.alaeri.command.serialization

import com.alaeri.command.serialization.entity.SerializedClass

interface IdOwner<Key>{
    val id: Key?
    val clazz: SerializedClass?
}