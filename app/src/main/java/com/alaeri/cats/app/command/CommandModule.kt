package com.alaeri.cats.app.command

import com.alaeri.cats.app.DefaultIRootCommandLogger
import com.alaeri.cats.app.Serializer
import com.alaeri.cats.app.command.focus.FocusCommandRepository
import com.alaeri.command.CommandState
import com.alaeri.command.core.Command
import com.alaeri.command.core.ICommandLogger
import com.alaeri.command.core.IInvokationContext
import com.alaeri.command.di.commandModule
import com.alaeri.command.history.id.IndexAndUUID
import org.koin.core.module.Module

object CommandModule{
    fun initWith(
        rootCommandContext: IInvokationContext<Any, Any>,
        serializer: Serializer<IndexAndUUID>
    ): Command<Module> =
        commandModule {
//            commandSingle<ICommandLogger<Any>> {
//                object : ICommandLogger<Any> {
//                    override fun log(commandState: CommandState<Any>) {
//                        rootCommandContext.emit(commandState)
//                    }
//                }
//            }
//            commandSingle<IInvokationContext<*, *>> { rootCommandContext }
            commandSingle<DefaultIRootCommandLogger> {
                serializer
            }
            commandSingle<CommandRepository> { CommandRepository() }
            commandSingle<FocusCommandRepository> { FocusCommandRepository(get()) }
        }
}
