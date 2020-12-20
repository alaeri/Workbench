package com.alaeri.log.serialize.serialize.representation

import com.alaeri.log.core.child.ChildTag
import com.alaeri.log.serialize.serialize.SerializedTag

data class FiliationRepresentation(
    val parentRepresentation: SerializedTag<*>
) : SerializedTag<ChildTag>