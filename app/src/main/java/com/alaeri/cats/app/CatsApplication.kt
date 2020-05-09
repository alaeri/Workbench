package com.alaeri.cats.app

import android.app.Application
import com.alaeri.cats.app.cats.catsModule
import com.alaeri.cats.app.command.CommandRepository
import com.alaeri.cats.app.command.commandListFragmentModule
import com.alaeri.cats.app.ui.cats.catsFragmentModule
import com.alaeri.cats.app.user.userModule
import com.alaeri.command.core.IInvokationContext
import com.alaeri.command.history.serialize
import com.alaeri.command.history.spread
import com.alaeri.command.history.id.DefaultIdStore
import com.alaeri.command.buildCommandContext
import com.alaeri.command.core.invoke
import com.alaeri.command.invokeSyncCommand
import defaultKey
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
class CatsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DefaultIdStore.create()
        val commandRepository: CommandRepository = CommandRepository()
        val operationContext =
            buildCommandContext<Any>(this) { state ->
                val flatList = spread(this, state)
                flatList.map {
                    val serialized =
                        serialize(it.operationContext, it.state, it.depth) { this.defaultKey() }
                    commandRepository.save(serialized)
                }
            }
        val commandModule = module {
            single<IInvokationContext<*, *>>{ operationContext }
            single {  commandRepository }
        }
        invokeSyncCommand(operationContext){
            startKoin {
                androidContext(this@CatsApplication)
                modules(commandModule,
                    invoke{ appModule },
                    invoke { userModule },
                    invoke { catsModule },
                    invoke { catsFragmentModule },
                    invoke { commandListFragmentModule })
            }
        }

    }


}