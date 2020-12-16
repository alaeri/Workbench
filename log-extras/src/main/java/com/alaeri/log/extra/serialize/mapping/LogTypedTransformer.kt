package com.alaeri.log.extra.serialize.mapping

import com.alaeri.log.core.context.LogContext
import com.alaeri.log.extra.serialize.LogRepresentation

interface LogTypedTransformer<InputType: LogContext, OutputType: LogRepresentation<InputType>>:
    TypedTransformer<InputType, OutputType>