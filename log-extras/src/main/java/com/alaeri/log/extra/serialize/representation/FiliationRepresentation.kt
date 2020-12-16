package com.alaeri.log.extra.serialize.representation

import com.alaeri.log.core.child.ChildLogContext
import com.alaeri.log.extra.serialize.LogRepresentation

data class FiliationRepresentation(
    val parentRepresentation: LogRepresentation<*>
) : LogRepresentation<ChildLogContext>