package com.alaeri.log.extra.serialize.mapping

import com.alaeri.log.extra.serialize.representation.EntityRepresentation
import kotlin.reflect.KClass

abstract class EntityTransformer<InputType: Any, OutputType: EntityRepresentation<InputType>>(clazz: KClass<InputType>):
    TypedTransformer<InputType, OutputType>(clazz)