package com.alaeri.log.serialize.serialize.representation

import com.alaeri.log.core.child.ChildLogContext
import com.alaeri.log.serialize.serialize.LogRepresentation

data class FiliationRepresentation(
    val parentRepresentation: LogRepresentation<*>
) : LogRepresentation<ChildLogContext>