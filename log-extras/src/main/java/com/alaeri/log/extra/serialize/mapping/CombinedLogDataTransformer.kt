package com.alaeri.log.extra.serialize.mapping

import com.alaeri.log.core.child.ChildLogContext
import com.alaeri.log.core.context.ListLogContext
import com.alaeri.log.core.context.LogContext
import com.alaeri.log.extra.serialize.LogRepresentation
import com.alaeri.log.extra.serialize.representation.FiliationRepresentation
import com.alaeri.log.extra.serialize.representation.ListRepresentation

class CombinedLogDataTransformer(private val innerTypedMappers: List<LogTypedTransformer<*, *>>):
    TypedTransformer<LogContext, LogRepresentation<LogContext>>(LogContext::class) {

    override fun transform(logContext: LogContext) : LogRepresentation<LogContext> {
        return when(logContext){
            is ListLogContext -> ListRepresentation(logContext.list.map { transform(it) })
            is ChildLogContext -> FiliationRepresentation(transform(logContext.parentLogContext)) as LogRepresentation<LogContext>
            else -> {
                val list = innerTypedMappers.mapNotNull {
                    it.transformOrNull(logContext)
                }
                ListRepresentation(list)
            }
        }
    }
}