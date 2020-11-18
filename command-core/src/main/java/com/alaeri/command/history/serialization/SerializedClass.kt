package com.alaeri.command.history.serialization

data class SerializedClass(val className: String, val simpleName: String){
    override fun toString(): String {
        return simpleName
    }
}