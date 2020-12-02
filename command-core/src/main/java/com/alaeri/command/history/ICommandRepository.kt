package com.alaeri.command.history

import com.alaeri.command.history.serialization.SerializableCommandStateAndContext
import kotlinx.coroutines.flow.Flow

/**
 * Created by Emmanuel Requier on 28/11/2020.
 */
interface ICommandRepository<Key>{
    val commands: Flow<List<SerializableCommandStateAndContext<Key>>>
    fun clear()
}