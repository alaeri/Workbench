package com.alaeri.log.core.serialize.mapping

import com.alaeri.log.core.context.LogContext
import com.alaeri.log.core.serialize.LogRepresentation

interface LogTypedTransformer<InputType: LogContext, OutputType: LogRepresentation<InputType>>:
    TypedTransformer<InputType, OutputType>