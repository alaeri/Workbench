package com.alaeri.command.repository

import com.alaeri.command.serialization.entity.SerializableCommandStateAndScope
import kotlinx.coroutines.flow.Flow

/**
 * Created by Emmanuel Requier on 28/11/2020.
 * TODO move this class to a command-visualization kotlin module
 *
 */
interface ICommandRepository<Key>{
    val commands: Flow<List<SerializableCommandStateAndScope<Key>>>
    fun clear()
}