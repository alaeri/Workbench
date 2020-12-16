package com.alaeri.log.extra.serialize

/**
 * Created by Emmanuel Requier on 13/12/2020.
 */
interface ILogSerializer {
    fun serialize(logDataAndState: LogDataAndState): SerializedLogDataAndState
}