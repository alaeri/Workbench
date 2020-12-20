package com.alaeri.log.serialize.serialize

import com.alaeri.log.core.Log

/**
 * Created by Emmanuel Requier on 13/12/2020.
 */
interface ILogSerializer {
    fun serialize(log: Log): SerializedLog
}