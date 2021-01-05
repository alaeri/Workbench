package com.alaeri.log.server

import com.alaeri.log.extra.tag.name.NameRepresentation
import com.alaeri.log.serialize.serialize.SerializedTag
import com.alaeri.log.serialize.serialize.representation.FiliationRepresentation
import com.alaeri.log.serialize.serialize.representation.ListRepresentation
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

object SerializedTagAdapter:  TypeAdapter<SerializedTag>(){

    override fun write(out: JsonWriter?, value: SerializedTag?) {
        out?.apply {
            beginObject()
            name("type")
            value(if(value!=null){ value::class.java.simpleName}else{null})
            name("uuid")
            value(value?.identity?.uuid)
            name("index")
            value(value?.identity?.index)
            when(value){
                is ListRepresentation -> {
                    name("list")
                    beginArray()
                    value.representations.forEach {
                        write(out, it)
                    }
                    endArray()
                }
                is FiliationRepresentation -> {
                    name("parent")
                    write(out, value.parentRepresentation)
                }
                is NameRepresentation -> {
                    name("name")
                    value(value.name)
                }
                else -> { }
            }
            endObject()
        }


    }

    override fun read(`in`: JsonReader?): SerializedTag {
        TODO("Not yet implemented")
    }
}