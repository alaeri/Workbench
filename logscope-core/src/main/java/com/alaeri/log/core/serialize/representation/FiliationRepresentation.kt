package com.alaeri.log.core.serialize.representation

import com.alaeri.log.core.context.ChildLogContext
import com.alaeri.log.core.serialize.LogRepresentation

data class FiliationRepresentation(
    val parentRepresentation: LogRepresentation<*>
) : LogRepresentation<ChildLogContext>