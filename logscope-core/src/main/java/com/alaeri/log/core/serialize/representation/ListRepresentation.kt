package com.alaeri.log.core.serialize.representation

import com.alaeri.log.core.context.LogContext
import com.alaeri.log.core.serialize.LogRepresentation

data class ListRepresentation(val representations: List<LogRepresentation<*>>) :
    LogRepresentation<LogContext>