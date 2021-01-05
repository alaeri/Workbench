package com.alaeri.log.serialize.serialize.mapping

import com.alaeri.log.core.Log.Tag
import com.alaeri.log.serialize.serialize.Identity
import com.alaeri.log.serialize.serialize.SerializedTag
import kotlin.reflect.KClass

abstract class TagTypedSerializer<InputType: Tag, OutputType: SerializedTag>(clazz: KClass<InputType>):
    TypedTransformer<InputType, OutputType>(clazz)