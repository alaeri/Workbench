package com.alaeri.log.serialize.serialize.mapping

import com.alaeri.log.serialize.serialize.representation.EntityRepresentation
import kotlin.reflect.KClass

abstract class EntityTransformer<InputType: Any, OutputType: EntityRepresentation<InputType>>(clazz: KClass<InputType>):
    TypedTransformer<InputType, OutputType>(clazz)