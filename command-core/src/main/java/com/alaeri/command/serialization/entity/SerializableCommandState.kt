package com.alaeri.command.serialization.entity

import com.alaeri.command.serialization.IdOwner

sealed class SerializableCommandState<Key>{
    data class Value<Key>(val valueId: Key,
                          override val clazz: SerializedClass?,
                          val description: String?): SerializableCommandState<Key>(),
        IdOwner<Key> {
        override val id = valueId
    }
    data class Done<Key>(val valueId: Key?, override val clazz: SerializedClass?, val description: String?) : SerializableCommandState<Key>(),
        IdOwner<Key> {
        override val id: Key? = valueId
    }
    data class Failure<Key>(val throwableId: Key, val throwableClass: SerializedClass?, val message: String?): SerializableCommandState<Key>(),
        IdOwner<Key> {
        override val id: Key = throwableId
        override val clazz: SerializedClass? = throwableClass
    }
    class Waiting<Key>: SerializableCommandState<Key>()
    data class Starting<Key>(override val id: Key) : SerializableCommandState<Key>(), IdOwner<Key> {
        override val clazz: SerializedClass? = null
    }
    data class Step<Key>(val name: String? = null): SerializableCommandState<Key>()
    data class Progress<Key>(val current: Number, val max: Number): SerializableCommandState<Key>()

    fun shortString(): String = when(this){
        is IdOwner<*> -> when {
            clazz?.simpleName == "Unit" -> when(this){
                is Value -> "Unit Value"
                is Done -> "Done (Unit)"
                else -> this.toString()
            }
            clazz?.simpleName == "BeanDefinition" -> when(this){
                is Value -> "Value: BeanDefinition: ${this.description}"
                is Done -> "Done: BeanDefinition: ${this.description}"
                else -> this.toString()
            }
            clazz?.simpleName == "Module" -> when(this){
                is Value -> "Value: Module: ${this.id}"
                is Done -> "Done: Module: ${this.id}"
                else -> this.toString()
            }
            else -> this.toString()
        }
        is Starting -> "Start"
        else -> this.toString()
    }
}