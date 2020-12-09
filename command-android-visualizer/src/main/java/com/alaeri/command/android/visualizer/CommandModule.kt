package com.alaeri.command.android.visualizer

import com.alaeri.command.ICommandLogger
import com.alaeri.command.serialization.GenericSerializer
import com.alaeri.command.android.visualizer.focus.FocusCommandRepository
import com.alaeri.command.serialization.id.IdBank
import com.alaeri.command.serialization.id.IndexAndUUID
import org.koin.dsl.module
import java.util.*

object CommandModule{

    val commandModule = module {
        single<ICommandLogger> {
            val idBank = IdBank<IndexAndUUID>(null) { previous ->
                IndexAndUUID(index = (previous?.index ?: 0) + 1, uuid = UUID.randomUUID().toString())
            }
            GenericSerializer<IndexAndUUID>(idBank, get<CommandRepository>())
        }
        single<CommandRepository> { CommandRepository() }
        single<FocusCommandRepository> { FocusCommandRepository(get()) }
    }
}
