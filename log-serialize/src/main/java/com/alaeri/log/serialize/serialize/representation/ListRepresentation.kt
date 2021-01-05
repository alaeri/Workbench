package com.alaeri.log.serialize.serialize.representation

import com.alaeri.log.core.Log
import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.serialize.serialize.Identity
import com.alaeri.log.serialize.serialize.SerializedTag
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(value = "list")
data class ListRepresentation(
    @Contextual val representations: List<SerializedTag>,
    @Contextual override val identity: IdentityRepresentation) :
    SerializedTag