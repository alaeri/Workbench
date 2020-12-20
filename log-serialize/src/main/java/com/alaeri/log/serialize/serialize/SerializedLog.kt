package com.alaeri.log.serialize.serialize

data class SerializedLog(
    val tag: SerializedTag<*>,
    val message: SerializedLogMessage
)