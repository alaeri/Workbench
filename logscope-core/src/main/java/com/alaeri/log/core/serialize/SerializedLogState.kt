package com.alaeri.log.core.serialize

import com.alaeri.log.core.serialize.representation.EntityRepresentation

sealed class SerializedLogState{
    class Start(parameters: List<EntityRepresentation<*>?>) : SerializedLogState()
    class Success(entityRepresentation: EntityRepresentation<*>?): SerializedLogState()
    class Error(exception: Throwable?): SerializedLogState()
}