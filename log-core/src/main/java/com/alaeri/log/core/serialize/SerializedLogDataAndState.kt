package com.alaeri.log.core.serialize

data class SerializedLogDataAndState(
    val data: LogRepresentation<*>,
    val state: SerializedLogState
)