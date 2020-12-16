package com.alaeri.log.extra.serialize

import com.alaeri.log.extra.serialize.representation.EntityRepresentation

sealed class SerializedLogState{
    class Start(parameters: List<EntityRepresentation<*>?>) : SerializedLogState()
    class Success(entityRepresentation: EntityRepresentation<*>?): SerializedLogState()
    class Error(exception: Throwable?): SerializedLogState()
}