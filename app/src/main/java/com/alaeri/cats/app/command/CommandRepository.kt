package com.alaeri.cats.app.command

import android.util.Log
import com.alaeri.command.history.SerializableCommandStateAndContext
import com.alaeri.command.history.id.IndexAndUUID

/**
 * Created by Emmanuel Requier on 04/05/2020.
 */
class CommandRepository {

    val list: MutableList<SerializableCommandStateAndContext<IndexAndUUID>> = mutableListOf()

    fun save(serializableCommandStateAndContext: SerializableCommandStateAndContext<IndexAndUUID>){
        list.add(serializableCommandStateAndContext)
        Log.d("OPERATION", serializableCommandStateAndContext.toString())
    }

}