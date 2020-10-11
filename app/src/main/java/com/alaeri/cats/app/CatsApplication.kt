package com.alaeri.cats.app

import android.app.Application
import android.util.Log
import com.alaeri.cats.app.cats.catsModule
import com.alaeri.cats.app.command.CommandModule
import com.alaeri.cats.app.command.CommandRepository
import com.alaeri.cats.app.command.commandListFragmentModule
import com.alaeri.cats.app.ui.cats.catsFragmentModule
import com.alaeri.cats.app.ui.viewpager.viewPagerFragmentModule
import com.alaeri.cats.app.user.userModule
import com.alaeri.command.CommandState
import com.alaeri.command.android.CommandNomenclature
import com.alaeri.command.buildCommandContextA
import com.alaeri.command.core.*
import com.alaeri.command.di.AbstractCommandLogger
import com.alaeri.command.di.DelayedLogger
import com.alaeri.command.di.invokeModules
import com.alaeri.command.history.id.DefaultIdStore
import com.alaeri.command.history.id.IndexAndUUID
import com.alaeri.command.history.serialization.SerializableCommandStateAndContext
import com.alaeri.command.history.serialize
import com.alaeri.command.history.spread
import com.alaeri.command.invokeSyncCommand
import defaultKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.definition.BeanDefinition
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.module

/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
@ExperimentalCoroutinesApi
class CatsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DefaultIdStore.create()

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        val mutableLoggerStateFlow = MutableStateFlow<AbstractCommandLogger<Command<Any>>?>(null)
        val mutableStateFlow = MutableStateFlow<AbstractCommandLogger<SerializableCommandStateAndContext<IndexAndUUID>>?>(null)
        val delayedSerializedCommandLogger = DelayedLogger(coroutineScope, mutableStateFlow)

        val rootCommandContext = buildCommandContextA<Any>(this,
        nomenclature = CommandNomenclature.Root,
        name = "root") { state ->
                val flatList = spread(this, state)
                flatList.map {
                    val serialized = serialize(it.operationContext, it.state, it.depth) { this.defaultKey() }
                    Log.d("COMMAND","$serialized")
                    delayedSerializedCommandLogger.log(serialized)
                }
            }

        invokeSyncCommand(rootCommandContext){
            val koinApp = startKoin {
                androidContext(this@CatsApplication)
                invokeModules(this@invokeSyncCommand,
                    CommandModule.initWith(rootCommandContext),
                    appModule,
                    viewPagerFragmentModule,
                    userModule,
                    catsModule,
                    catsFragmentModule,
                    commandListFragmentModule
                    )
            }
            mutableLoggerStateFlow.value = koinApp.koin.get<AbstractCommandLogger<Command<Any>>>()
            mutableStateFlow.value = koinApp.koin.get<CommandRepository>()
            Unit
        }

    }



}