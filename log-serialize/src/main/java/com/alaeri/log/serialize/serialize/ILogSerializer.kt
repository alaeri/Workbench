package com.alaeri.log.serialize.serialize

import com.alaeri.log.core.Log

/**
 * Created by Emmanuel Requier on 13/12/2020.
 */
interface ILogSerializer<I: Identity> {
    fun serialize(log: Log): SerializedLog<I>
}