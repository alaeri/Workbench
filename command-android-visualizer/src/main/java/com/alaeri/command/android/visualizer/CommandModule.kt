package com.alaeri.command.android.visualizer

import com.alaeri.command.DefaultIRootCommandLogger
import com.alaeri.command.Serializer
import com.alaeri.command.android.visualizer.focus.FocusCommandRepository
import com.alaeri.command.core.Command
import com.alaeri.command.history.id.IdBank
import com.alaeri.command.history.id.IndexAndUUID
import com.alaeri.command.koin.commandModule
import org.koin.core.module.Module
import java.util.*

object CommandModule{
    val commandModule: Command<Module> =
        CommandModule.commandModule {
            commandSingle<DefaultIRootCommandLogger> {
                val idBank = IdBank<IndexAndUUID>(null) { previous ->
                    IndexAndUUID(index = (previous?.index ?: 0) + 1, uuid = UUID.randomUUID())
                }
                Serializer<IndexAndUUID>(idBank, get<CommandRepository>())
            }
            commandSingle<CommandRepository> { CommandRepository() }
            commandSingle<FocusCommandRepository> { FocusCommandRepository(get()) }
        }
}
