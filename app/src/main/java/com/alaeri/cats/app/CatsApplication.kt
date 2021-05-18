package com.alaeri.cats.app

import androidx.multidex.MultiDexApplication
import com.alaeri.cats.app.cats.catsModule
import com.alaeri.cats.app.ui.cats.catsFragmentModule
import com.alaeri.cats.app.ui.viewpager.viewPagerFragmentModule
import com.alaeri.cats.app.user.userModule
import com.alaeri.log.core.Log
import com.alaeri.log.core.LogConfig
import com.alaeri.log.core.LogEnvironmentFactory
import com.alaeri.log.core.child.ChildLogEnvironmentFactory
import com.alaeri.log.core.context.EmptyTag
import com.alaeri.cats.app.collector
import com.alaeri.cats.app.logBlocking
import com.alaeri.log.android.ui.LogAndroidUiModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
@ExperimentalCoroutinesApi
class CatsApplication : MultiDexApplication() {

    init {

    }


    override fun onCreate() {
        super.onCreate()
        LogConfig.logEnvironmentFactory = ChildLogEnvironmentFactory
        val env = LogConfig.logEnvironmentFactory.blockingLogEnvironment(EmptyTag(), collector)
        env.prepare()


        logBlocking<Unit>(name="init"){
            val koinApp = startKoin {

                androidContext(this@CatsApplication)
                modules(
                    appModule,
                    viewPagerFragmentModule,
                    userModule,
                    catsModule,
                    catsFragmentModule,
                    LogAndroidUiModule.logAndroidUiModule(logRepository)
                )



//                modules(commandModule, commandListFragmentModule, CommandOptionsFragmentModule().module)
            }
            Unit
        }
    }
}

