package com.alaeri.log.serialize.serialize

/**
 * Created by Emmanuel Requier on 13/12/2020.
 */
interface ILogSerializer {
    fun serialize(logDataAndState: LogDataAndState): SerializedLogDataAndState
}