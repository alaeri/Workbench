package com.alaeri.cats.app.command

import com.alaeri.cats.app.DefaultIRootCommandLogger
import com.alaeri.cats.app.Serializer
import com.alaeri.cats.app.command.focus.FocusCommandRepository
import com.alaeri.command.CommandState
import com.alaeri.command.core.Command
import com.alaeri.command.core.ICommandLogger
import com.alaeri.command.core.IInvokationContext
import com.alaeri.command.di.commandModule
import com.alaeri.command.history.id.IdBank
import com.alaeri.command.history.id.IndexAndUUID
import org.koin.core.module.Module
import java.util.*

object CommandModule{
    val commandModule: Command<Module> =
        commandModule {
            commandSingle<DefaultIRootCommandLogger> {
                val idBank = IdBank<IndexAndUUID>(null) { previous ->
                    IndexAndUUID(index = (previous?.index?:0) +1, uuid = UUID.randomUUID())
                }
                Serializer<IndexAndUUID>(idBank, get<CommandRepository>())
            }
            commandSingle<CommandRepository> { CommandRepository() }
            commandSingle<FocusCommandRepository> { FocusCommandRepository(get()) }
        }
}
