package com.alaeri.log.extra.tag.receiver

import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.extra.identity.IdentityTransformer
import com.alaeri.log.extra.type.TypeTypedTransformer
import com.alaeri.log.serialize.serialize.mapping.TagTypedSerializer
import com.alaeri.log.serialize.serialize.mapping.TypedTransformer

/**
 * Created by Emmanuel Requier on 20/12/2020.
 */
class ReceiverTranformer(
    private val identityTransformer: IdentityTransformer,
    private val typeTranformer: TypeTypedTransformer)
    : TagTypedSerializer<ReceiverTag, ReceiverRepresentation>(ReceiverTag::class) {

    override fun transform(logData: ReceiverTag): ReceiverRepresentation {
        return ReceiverRepresentation(
            typeTranformer.transform(logData.receiver),
            identityTransformer.transform(logData.receiver))
    }
}