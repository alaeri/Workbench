package com.alaeri.command.android.visualizer

import com.alaeri.command.DefaultIRootCommandLogger
import com.alaeri.command.Serializer
import com.alaeri.command.android.visualizer.focus.FocusCommandRepository
import com.alaeri.command.history.id.IdBank
import com.alaeri.command.history.id.IndexAndUUID
import org.koin.dsl.module
import java.util.*

object CommandModule{

    val commandModule = module {
        single<DefaultIRootCommandLogger> {
            val idBank = IdBank<IndexAndUUID>(null) { previous ->
                IndexAndUUID(index = (previous?.index ?: 0) + 1, uuid = UUID.randomUUID().toString())
            }
            Serializer<IndexAndUUID>(idBank, get<CommandRepository>())
        }
        single<CommandRepository> { CommandRepository() }
        single<FocusCommandRepository> { FocusCommandRepository(get()) }
    }
}
