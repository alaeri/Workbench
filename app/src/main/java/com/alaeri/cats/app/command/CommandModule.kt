package com.alaeri.cats.app.command

import com.alaeri.command.CommandState
import com.alaeri.command.core.Command
import com.alaeri.command.core.ICommandLogger
import com.alaeri.command.core.IInvokationContext
import com.alaeri.command.di.AbstractCommandLogger
import com.alaeri.command.di.commandModule
import org.koin.core.module.Module

object CommandModule{
    fun initWith(
        rootCommandContext: IInvokationContext<Any, Any>
    ): Command<Module> =
        commandModule {
            commandSingle<ICommandLogger<Any>> {
                object : ICommandLogger<Any> {
                    override fun log(commandState: CommandState<Any>) {
                        rootCommandContext.emit(commandState)
                    }
                }
            }
            commandSingle<AbstractCommandLogger<Command<Any>>> {
                get<ICommandLogger<Any>>()  as AbstractCommandLogger<Command<Any>>
            }
            commandSingle<IInvokationContext<*, *>> { rootCommandContext }
            commandSingle<CommandRepository> { CommandRepository() }
        }
}
