package com.alaeri.log.serialize.serialize

import com.alaeri.log.serialize.serialize.representation.EntityRepresentation

sealed class SerializedLogMessage{
    class Start(parameters: List<EntityRepresentation<*>?>) : SerializedLogMessage()
    class Success(entityRepresentation: EntityRepresentation<*>?): SerializedLogMessage()
    class Error(throwableRepresentation: EntityRepresentation<*>?): SerializedLogMessage()
}