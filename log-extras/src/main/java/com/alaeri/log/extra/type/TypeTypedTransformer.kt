package com.alaeri.log.extra.type

import com.alaeri.log.serialize.serialize.mapping.TypedTransformer

/**
 * Created by Emmanuel Requier on 20/12/2020.
 *
 *
 */
class TypeTypedTransformer: TypedTransformer<Any, TypeRepresentation>(Any::class) {

    override fun transform(logData: Any): TypeRepresentation {
        return TypeRepresentation(logData.javaClass)
    }
}