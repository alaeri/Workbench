package com.alaeri.log.core.serialize.mapping

import com.alaeri.log.core.serialize.representation.EntityRepresentation

interface EntityTransformer<InputType, OutputType: EntityRepresentation<InputType>>:
    TypedTransformer<InputType, OutputType> {
}