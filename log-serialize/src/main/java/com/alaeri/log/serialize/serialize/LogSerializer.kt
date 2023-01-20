package com.alaeri.log.serialize.serialize

import com.alaeri.log.core.Log
import com.alaeri.log.core.Log.Tag
import com.alaeri.log.core.Log.Message
import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.serialize.serialize.mapping.EntityTransformer
import com.alaeri.log.serialize.serialize.mapping.TagTypedSerializer
import com.alaeri.log.serialize.serialize.representation.EntityRepresentation

class LogSerializer(
    private val logTypedMapper: TagTypedSerializer<Tag, SerializedTag>,
    private val entityTransformer: EntityTransformer<Any, EntityRepresentation<Any>>,
    private val iIdentityTransformer: IIdentityTransformer<IdentityRepresentation>
) : ILogSerializer<IdentityRepresentation>{

    override fun serialize(log: Log): SerializedLog<IdentityRepresentation> {
        val state = log.message
        return SerializedLog(
            logTypedMapper.transformOrNull(log.tag) ?: EmptySerializedTag(iIdentityTransformer.transform(log.tag)),
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
                is Message.OnEach -> SerializedLogMessage.OnEach(state.it?.let{ entityTransformer.transform(it) })
            }
        )
    }
}