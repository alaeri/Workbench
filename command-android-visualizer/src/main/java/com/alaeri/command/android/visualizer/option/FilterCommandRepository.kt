package com.alaeri.command.android.visualizer.option

import com.alaeri.command.CommandNomenclature
import com.alaeri.command.android.visualizer.CommandRepository
import com.alaeri.command.serialization.id.IndexAndUUID
import com.alaeri.command.serialization.entity.SerializableCommandStateAndScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

class FilterCommandRepository(private val commandRepository: CommandRepository){

    private val mutableOptions = MutableStateFlow(VisualizationOptions(true, true))

    val options : Flow<VisualizationOptions> = mutableOptions
    fun update(options: VisualizationOptions){
        mutableOptions.value = options
    }

    val list: Flow<List<SerializableCommandStateAndScope<IndexAndUUID>>> = mutableOptions.mapLatest{ options ->
        commandRepository.list.filter { serialized ->
            when(serialized.scope.commandNomenclature){
                is CommandNomenclature.Injection -> options.showInjection
                is CommandNomenclature.Android.Lifecycle -> options.showLifecycle
                else -> true
            }
        }
    }.flowOn(Dispatchers.Default)

}