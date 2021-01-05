package com.alaeri.log.extra.tag.receiver

import com.alaeri.log.extra.identity.IdOwner
import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.extra.type.TypeRepresentation
import com.alaeri.log.serialize.serialize.Identity
import com.alaeri.log.serialize.serialize.SerializedTag

/**
 * Created by Emmanuel Requier on 19/12/2020.
 */
data class ReceiverRepresentation(
    val type: TypeRepresentation,
    override val identity: IdentityRepresentation
): IdOwner, SerializedTag