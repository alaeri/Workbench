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
import com.alaeri.command.buildCommandRoot
import com.alaeri.command.core.*
import com.alaeri.command.di.AbstractCommandLogger
import com.alaeri.command.di.DelayedLogger
import com.alaeri.command.di.invokeModules
import com.alaeri.command.history.id.DefaultIdStore
import com.alaeri.command.history.id.IdBank
import com.alaeri.command.history.id.IndexAndUUID
import com.alaeri.command.history.serialization.SerializableCommandStateAndContext
import com.alaeri.command.history.serialize
import com.alaeri.command.history.spread
import com.alaeri.command.invokeSyncCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.*

/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
@ExperimentalCoroutinesApi
class CatsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DefaultIdStore.create()

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        val mutableLoggerStateFlow = MutableStateFlow<DefaultIRootCommandLogger?>(null)
        val mutableStateFlow = MutableStateFlow<AbstractCommandLogger<SerializableCommandStateAndContext<IndexAndUUID>>?>(null)
        val delayedSerializedCommandLogger = DelayedLogger(coroutineScope, mutableStateFlow)
        val idBank = IdBank<IndexAndUUID>(null) { previous ->
            IndexAndUUID(index = (previous?.index?:0) +1, uuid = UUID.randomUUID())
        }
        val serializer = Serializer<IndexAndUUID>(idBank, delayedSerializedCommandLogger)
        val rootCommandContext = buildCommandRoot(this,
        nomenclature = CommandNomenclature.Root,
        name = "root") { state ->
            serializer.log(this, state)
        }

        invokeSyncCommand(rootCommandContext){
            val koinApp = startKoin {
                androidContext(this@CatsApplication)
                invokeModules(this@invokeSyncCommand,
                    CommandModule.initWith(rootCommandContext, serializer),
                    appModule,
                    viewPagerFragmentModule,
                    userModule,
                    catsModule,
                    catsFragmentModule,
                    commandListFragmentModule
                    )
            }
            mutableLoggerStateFlow.value = koinApp.koin.get<DefaultIRootCommandLogger>()
            mutableStateFlow.value = koinApp.koin.get<CommandRepository>()
            Unit
        }

    }



}
interface IRootCommandLogger<Key>{
    fun log(context: IInvokationContext<*, *>, state: CommandState<*>)
}
typealias DefaultIRootCommandLogger = IRootCommandLogger<IndexAndUUID>

class Serializer<Key>(private val idBank: IdBank<Key>,
                      private val delayedLogger: DelayedLogger<SerializableCommandStateAndContext<Key>>) : DefaultIRootCommandLogger{

    override fun log(context: IInvokationContext<*, *>, state: CommandState<*>){
        val flatList = spread(context, state, 0, context)
        flatList.map {
            val serialized = serialize(it.parentContext, it.operationContext, it.state, it.depth) { idBank.keyOf(this) }
            Log.d("COMMAND","$serialized")
            delayedLogger.log(serialized)
        }
    }
}