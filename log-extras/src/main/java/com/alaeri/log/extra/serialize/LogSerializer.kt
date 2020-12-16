package com.alaeri.log.extra.serialize

import com.alaeri.log.core.context.LogContext
import com.alaeri.log.core.LogState
import com.alaeri.log.extra.serialize.mapping.EntityTransformer
import com.alaeri.log.extra.serialize.mapping.LogTypedTransformer
import com.alaeri.log.extra.serialize.representation.EntityRepresentation

class LogSerializer(
    private val logTypedMapper: LogTypedTransformer<LogContext, LogRepresentation<LogContext>>,
    private val entityTransformer: EntityTransformer<Any, EntityRepresentation<Any>>
) : ILogSerializer{

    override fun serialize(logDataAndState: LogDataAndState): SerializedLogDataAndState{
        val state = logDataAndState.logState
        return SerializedLogDataAndState(
            logTypedMapper.transformOrNull(logDataAndState.logContext) ?: EmptyLogRepresentation(),
            when (state) {
                is LogState.Done<*> -> SerializedLogState.Success(state.result?.let {
                    entityTransformer.transform(
                        it
                    )
                })
                is LogState.Starting -> SerializedLogState.Start(state.params.map {
                    it?.let {
                        entityTransformer.transform(
                            it
                        )
                    }
                })
                is LogState.Failed -> SerializedLogState.Error(state.exception)
            }
        )
    }
}