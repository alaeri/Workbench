package com.alaeri.command.graph.command

import com.alaeri.command.CommandNomenclature
import com.alaeri.command.serialization.id.IndexAndUUID
import com.alaeri.command.serialization.entity.SerializedClass

sealed class GraphElement(open val key: IndexAndUUID){
    data class Receiver(override val key: IndexAndUUID, val serializedClass: SerializedClass): GraphElement(key)
    data class Command(override val key: IndexAndUUID, val commandNomenclature: CommandNomenclature, val name: String?): GraphElement(key)
    data class State(override val key: IndexAndUUID, val serializedClass: SerializedClass?): GraphElement(key)

    fun toStr() : String= when(this){
        is Receiver -> "${key.index} $serializedClass"
        is Command -> "${key.index} ${if(commandNomenclature != CommandNomenclature.Undefined){ commandNomenclature.javaClass.simpleName}else {""}} ${name ?: ""}"
        is State -> "${key.index}" + if(this.serializedClass!= null){" $serializedClass" }else{""}
    }
}