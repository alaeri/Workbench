package com.alaeri.log.serialize.serialize.representation

import com.alaeri.log.core.context.LogContext
import com.alaeri.log.serialize.serialize.LogRepresentation

data class ListRepresentation(val representations: List<LogRepresentation<*>>) :
    LogRepresentation<LogContext>