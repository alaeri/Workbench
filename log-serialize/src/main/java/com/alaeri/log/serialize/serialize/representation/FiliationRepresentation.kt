package com.alaeri.log.serialize.serialize.representation

import com.alaeri.log.core.child.ChildTag
import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.serialize.serialize.Identity
import com.alaeri.log.serialize.serialize.SerializedTag

data class FiliationRepresentation(
    val parentRepresentation: SerializedTag,
    override val identity: IdentityRepresentation
) : SerializedTag

data class ParentRepresentation(
    val childRep: SerializedTag,
    override val identity: IdentityRepresentation
) : SerializedTag