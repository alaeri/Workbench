package com.alaeri.log.extra.identity

import com.alaeri.log.serialize.serialize.Representation

data class IdentityRepresentation(val index: Int, val uuid: String): Representation<Any> {
    override fun toString(): String ="$index"//${""}"//uuid.toString().substring(0..8)}"
}