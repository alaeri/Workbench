package com.alaeri.log.serialize.serialize

/**
 * Created by Emmanuel Requier on 03/01/2021.
 */
interface IIdentityTransformer<IdentityType: Identity> {
    fun transform(identity: Any): IdentityType
}