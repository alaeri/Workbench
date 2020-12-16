package com.alaeri.log.extra.serialize.mapping

import com.alaeri.log.extra.serialize.representation.EntityRepresentation

interface EntityTransformer<InputType, OutputType: EntityRepresentation<InputType>>:
    TypedTransformer<InputType, OutputType> {
}