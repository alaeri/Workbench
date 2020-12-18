package com.alaeri.log.extra.serialize.mapping

import com.alaeri.log.core.context.LogContext
import com.alaeri.log.extra.serialize.LogRepresentation
import kotlin.reflect.KClass

abstract class LogTypedTransformer<InputType: LogContext, OutputType: LogRepresentation<InputType>>(clazz: KClass<InputType>):
    TypedTransformer<InputType, OutputType>(clazz)