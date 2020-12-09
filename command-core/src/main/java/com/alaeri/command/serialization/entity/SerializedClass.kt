package com.alaeri.command.serialization.entity

data class SerializedClass(val className: String, val simpleName: String){
    override fun toString(): String {
        return simpleName
    }
}