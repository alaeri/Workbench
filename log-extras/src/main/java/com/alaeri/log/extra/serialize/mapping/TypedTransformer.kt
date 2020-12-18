package com.alaeri.log.extra.serialize.mapping

import com.alaeri.log.extra.serialize.Representation
import java.lang.Exception
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Created by Emmanuel Requier on 13/12/2020.
 * TODO: find out how to improve this transformer
 */
abstract class TypedTransformer<InputType: Any, OutputType: Representation<InputType>>(private val supportedType: KClass<InputType>){

    abstract fun transform(logData: InputType) : OutputType

    fun transformOrNull(logContext: Any): OutputType? {
        val sup = supportedType
        val castOrNull : InputType? = try{ supportedType.cast(logContext) }catch (e: Exception){null}
        return castOrNull?.let {
            transform(castOrNull)
        }
    }
}