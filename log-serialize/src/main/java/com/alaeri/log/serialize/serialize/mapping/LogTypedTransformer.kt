package com.alaeri.log.serialize.serialize.mapping

import com.alaeri.log.core.context.LogContext
import com.alaeri.log.serialize.serialize.LogRepresentation
import kotlin.reflect.KClass

abstract class LogTypedTransformer<InputType: LogContext, OutputType: LogRepresentation<InputType>>(clazz: KClass<InputType>):
    TypedTransformer<InputType, OutputType>(clazz)