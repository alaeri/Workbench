package com.alaeri.log.extra.identity

import com.alaeri.log.serialize.serialize.Identity
import com.alaeri.log.serialize.serialize.Representation
import kotlinx.serialization.Serializable

@Serializable
data class IdentityRepresentation(val index: Int, val uuid: String): Representation<Any>, Identity {
    override fun toString(): String ="$index"//${""}"//uuid.toString().substring(0..8)}"
}