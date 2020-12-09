package com.alaeri.command.serialization.id

data class IndexAndUUID(val index: Int, val uuid: String){
    override fun toString(): String ="$index"//${""}"//uuid.toString().substring(0..8)}"
}