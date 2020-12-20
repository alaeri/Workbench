package com.alaeri.log.serialize.serialize.representation

import com.alaeri.log.core.Log
import com.alaeri.log.serialize.serialize.SerializedTag

data class ListRepresentation(val representations: List<SerializedTag<*>>) :
    SerializedTag<Log.Tag>