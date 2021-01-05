package com.alaeri.log.serialize.serialize.mapping

import com.alaeri.log.serialize.serialize.Representation
import java.lang.Exception
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Created by Emmanuel Requier on 13/12/2020.
 *
 */
abstract class TypedTransformer<InputType: Any, OutputType: Representation<Any>>(private val supportedType: KClass<InputType>){

    abstract fun transform(logData: InputType) : OutputType

    //TODO: find out how to improve this transform function the cast seems meh
    fun transformOrNull(logContext: Any): OutputType? {
        val sup = supportedType
        val castOrNull : InputType? = try{ supportedType.cast(logContext) }catch (e: Exception){null}
        return castOrNull?.let {
            transform(castOrNull)
        }
    }
}