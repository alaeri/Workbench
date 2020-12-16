package com.alaeri.log.extra.serialize

data class SerializedLogDataAndState(
    val data: LogRepresentation<*>,
    val state: SerializedLogState
)