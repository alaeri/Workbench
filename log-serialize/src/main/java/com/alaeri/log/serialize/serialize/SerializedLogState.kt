package com.alaeri.log.serialize.serialize

import com.alaeri.log.serialize.serialize.representation.EntityRepresentation

sealed class SerializedLogState{
    class Start(parameters: List<EntityRepresentation<*>?>) : SerializedLogState()
    class Success(entityRepresentation: EntityRepresentation<*>?): SerializedLogState()
    class Error(throwableRepresentation: EntityRepresentation<*>?): SerializedLogState()
}