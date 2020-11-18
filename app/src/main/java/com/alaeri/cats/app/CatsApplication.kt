package com.alaeri.cats.app

import androidx.multidex.MultiDexApplication
import com.alaeri.cats.app.cats.catsModule
import com.alaeri.cats.app.ui.cats.catsFragmentModule
import com.alaeri.cats.app.ui.viewpager.viewPagerFragmentModule
import com.alaeri.cats.app.user.userModule
import com.alaeri.command.*
import com.alaeri.command.CommandNomenclature
import com.alaeri.command.android.visualizer.CommandModule.commandModule
import com.alaeri.command.android.visualizer.commandListFragmentModule
import com.alaeri.command.di.DelayedCommandLogger
import com.alaeri.command.koin.invokeModules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

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

