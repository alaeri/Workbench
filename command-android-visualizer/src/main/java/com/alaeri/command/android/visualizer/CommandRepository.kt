package com.alaeri.command.android.visualizer

import android.util.Log
import com.alaeri.command.GenericLogger
import com.alaeri.command.serialization.entity.SerializableCommandStateAndScope
import com.alaeri.command.serialization.id.IndexAndUUID

/**
 * Created by Emmanuel Requier on 04/05/2020.
 */
typealias IDefaultSerializedCommandLogger = GenericLogger<SerializableCommandStateAndScope<IndexAndUUID>>
class CommandRepository : IDefaultSerializedCommandLogger {

    val list : List<SerializableCommandStateAndScope<IndexAndUUID>>
        get() = _list.toList()
    private val _list: MutableList<SerializableCommandStateAndScope<IndexAndUUID>> = mutableListOf()

    override fun log(value: SerializableCommandStateAndScope<IndexAndUUID>) {
        _list.add(value)
        Log.d("OPERATION", value.toString())

    }

}