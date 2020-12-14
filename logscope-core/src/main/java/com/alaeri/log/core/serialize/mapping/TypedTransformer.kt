package com.alaeri.log.core.serialize.mapping

import com.alaeri.log.core.context.LogContext
import com.alaeri.log.core.serialize.Representation

/**
 * Created by Emmanuel Requier on 13/12/2020.
 */
interface TypedTransformer<InputType, OutputType: Representation<InputType>>{
    val supportedType: Class<InputType>
    fun transform(logData: InputType) : OutputType
    fun transformOrNull(logContext: LogContext): OutputType? {
        val castOrNull = logContext as? InputType
        return castOrNull?.let {
            transform(logContext)
        }
    }
}