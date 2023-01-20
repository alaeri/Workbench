package com.alaeri.log.serialize.serialize

import com.alaeri.log.serialize.serialize.representation.EntityRepresentation

sealed class SerializedLogMessage{
    data class Start(val parameters: List<EntityRepresentation<*>?>) : SerializedLogMessage()
    data class Success(val entityRepresentation: EntityRepresentation<*>?): SerializedLogMessage()
    data class Error(val throwableRepresentation: EntityRepresentation<*>?): SerializedLogMessage()
    data class OnEach(val item: EntityRepresentation<*>?) : SerializedLogMessage()
}