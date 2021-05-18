package com.alaeri.log.serialize.serialize

data class SerializedLog<I: Identity>(
    val tag: SerializedTag,
    val message: SerializedLogMessage,
    val time: Long = System.currentTimeMillis()
)