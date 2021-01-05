package com.alaeri.log.extra.tag.callsite

import com.alaeri.log.extra.identity.IdentityTransformer
import com.alaeri.log.serialize.serialize.mapping.TagTypedSerializer

/**
 * Created by Emmanuel Requier on 05/01/2021.
 */
class CallSiteTransformer(private val identityTransformer: IdentityTransformer):TagTypedSerializer<CallSiteTag, CallSiteRepresentation>(CallSiteTag::class) {
    override fun transform(logData: CallSiteTag): CallSiteRepresentation {
        return CallSiteRepresentation(identityTransformer.transform(logData))
    }
}