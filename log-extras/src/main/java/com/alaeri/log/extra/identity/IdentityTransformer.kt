package com.alaeri.log.extra.identity

import com.alaeri.log.extra.identity.utils.IdBank
import com.alaeri.log.serialize.serialize.mapping.TypedTransformer

/**
 * Created by Emmanuel Requier on 19/12/2020.
 */
class IdentityTransformer(private val idBank: IdBank<IdentityRepresentation>): TypedTransformer<Any, IdentityRepresentation>(Any::class) {

    override fun transform(logData: Any): IdentityRepresentation {
         return idBank.keyOf(logData)
    }

}
