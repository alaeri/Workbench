package com.alaeri.cats.app.command

import android.util.Log
import com.alaeri.command.di.AbstractCommandLogger
import com.alaeri.command.history.serialization.SerializableCommandStateAndContext
import com.alaeri.command.history.id.IndexAndUUID

/**
 * Created by Emmanuel Requier on 04/05/2020.
 */
class CommandRepository : AbstractCommandLogger<SerializableCommandStateAndContext<IndexAndUUID>> {

    val list : List<SerializableCommandStateAndContext<IndexAndUUID>>
        get() = _list.toList()
    private val _list: MutableList<SerializableCommandStateAndContext<IndexAndUUID>> = mutableListOf()

    override fun log(value: SerializableCommandStateAndContext<IndexAndUUID>) {
        _list.add(value)
        Log.d("OPERATION", value.toString())

    }

}