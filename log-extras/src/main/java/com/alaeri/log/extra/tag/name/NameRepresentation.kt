package com.alaeri.log.extra.tag.name

import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.serialize.serialize.Identity
import com.alaeri.log.serialize.serialize.SerializedTag
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class NameRepresentation(@Contextual override val identity: IdentityRepresentation, val name: String): SerializedTag
