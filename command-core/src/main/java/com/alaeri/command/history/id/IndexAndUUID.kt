package com.alaeri.command.history.id

import java.util.*

data class IndexAndUUID(val index: Int, val uuid: String){
    override fun toString(): String ="$index"//${""}"//uuid.toString().substring(0..8)}"
}