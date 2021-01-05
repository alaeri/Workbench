package com.alaeri.log.serialize.serialize

import com.alaeri.log.core.Log
import com.alaeri.log.extra.identity.IdentityRepresentation
import kotlinx.serialization.PolymorphicSerializer

interface Identity
interface IdOwner{
    val identity: IdentityRepresentation
}

interface SerializedTag: Representation<Any>, IdOwner{

}
val serializer = PolymorphicSerializer<SerializedTag>(SerializedTag::class).apply {

}