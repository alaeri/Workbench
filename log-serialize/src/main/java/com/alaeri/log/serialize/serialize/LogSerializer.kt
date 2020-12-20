package com.alaeri.log.serialize.serialize

import com.alaeri.log.core.Log
import com.alaeri.log.core.Log.Tag
import com.alaeri.log.core.Log.Message
import com.alaeri.log.serialize.serialize.mapping.EntityTransformer
import com.alaeri.log.serialize.serialize.mapping.TagTypedSerializer
import com.alaeri.log.serialize.serialize.representation.EntityRepresentation

class LogSerializer(
    private val logTypedMapper: TagTypedSerializer<Tag, SerializedTag<Tag>>,
    private val entityTransformer: EntityTransformer<Any, EntityRepresentation<Any>>
) : ILogSerializer{

    override fun serialize(log: Log): SerializedLog{
        val state = log.message
        return SerializedLog(
            logTypedMapper.transformOrNull(log.tag) ?: EmptySerializedTag(),
            when (state) {
                is Message.Done<*> -> SerializedLogMessage.Success(state.result?.let {
                    entityTransformer.transform(
                        it
                    )
                })
                is Message.Starting -> SerializedLogMessage.Start(state.params.map {
                    it?.let {
                        entityTransformer.transform(
                            it
                        )
                    }
                })
                is Message.Failed -> SerializedLogMessage.Error(state.exception?.let{ entityTransformer.transform(it) })
            }
        )
    }
}