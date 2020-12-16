package com.alaeri.log.extra.serialize.representation

import com.alaeri.log.core.context.LogContext
import com.alaeri.log.extra.serialize.LogRepresentation

data class ListRepresentation(val representations: List<LogRepresentation<*>>) :
    LogRepresentation<LogContext>