package com.alaeri.log.extra.tag.callsite

import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.serialize.serialize.Identity
import com.alaeri.log.serialize.serialize.SerializedTag

data class CallSiteRepresentation(override val identity: IdentityRepresentation): SerializedTag