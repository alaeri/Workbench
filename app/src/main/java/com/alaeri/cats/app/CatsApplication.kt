package com.alaeri.cats.app

import android.app.Application
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.alaeri.cats.app.cats.catsModule
import com.alaeri.cats.app.command.CommandModule
import com.alaeri.cats.app.command.CommandModule.commandModule
import com.alaeri.cats.app.command.CommandRepository
import com.alaeri.cats.app.command.commandListFragmentModule
import com.alaeri.cats.app.ui.cats.catsFragmentModule
import com.alaeri.cats.app.ui.viewpager.viewPagerFragmentModule
import com.alaeri.cats.app.user.userModule
import com.alaeri.command.*
import com.alaeri.command.android.CommandNomenclature
import com.alaeri.command.core.*
import com.alaeri.command.di.AbstractCommandLogger
import com.alaeri.command.di.DelayedCommandLogger
import com.alaeri.command.di.DelayedLogger
import com.alaeri.command.di.invokeModules
import com.alaeri.command.history.id.DefaultIdStore
import com.alaeri.command.history.id.IdBank
import com.alaeri.command.history.id.IndexAndUUID
import com.alaeri.command.history.serialization.SerializableCommandStateAndContext
import com.alaeri.command.history.serialize
import com.alaeri.command.history.spread
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
class CatsApplication : MultiDexApplication(), ICommandRootOwner {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val mutableLoggerStateFlow = MutableStateFlow<DefaultIRootCommandLogger?>(null)
    private val delayedCommandLogger = DelayedCommandLogger(coroutineScope, mutableLoggerStateFlow)

    override val commandRoot: AnyCommandRoot = buildCommandRoot(this,
        nomenclature = CommandNomenclature.Root,
        name = "root", iRootCommandLogger = delayedCommandLogger)


    override fun onCreate() {
        super.onCreate()

        invokeRootCommand<Unit>(name="init", commandNomenclature = CommandNomenclature.Root){
            val koinApp = startKoin {
                androidContext(this@CatsApplication)
                invokeModules(this@invokeRootCommand,
                    commandModule,
                    appModule,
                    viewPagerFragmentModule,
                    userModule,
                    catsModule,
                    catsFragmentModule,
                    commandListFragmentModule
                    )
            }
            mutableLoggerStateFlow.value = koinApp.koin.get<DefaultIRootCommandLogger>()
            //mutableStateFlow.value = koinApp.koin.get<CommandRepository>()
            Unit
        }
    }



}
interface DefaultIRootCommandLogger {
    fun log(context: IInvokationContext<*, *>, state: CommandState<*>)
}

class Serializer<Key>(private val idBank: IdBank<Key>,
                      private val delayedLogger: AbstractCommandLogger<SerializableCommandStateAndContext<Key>>) : DefaultIRootCommandLogger{

    override fun log(context: IInvokationContext<*, *>, state: CommandState<*>){
        val flatList = spread(context, state, 0, context)
        flatList.map {
            val serialized = serialize(it.parentContext, it.operationContext, it.state, it.depth) { idBank.keyOf(this) }
            Log.d("COMMAND","$serialized")
            delayedLogger.log(serialized)
        }
    }
}