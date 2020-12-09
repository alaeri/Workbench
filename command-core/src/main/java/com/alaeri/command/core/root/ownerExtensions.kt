package com.alaeri.command.core.root

import com.alaeri.command.CommandNomenclature
import com.alaeri.command.CommandState
import com.alaeri.command.core.ChainableCommandScope
import com.alaeri.command.core.Command
import com.alaeri.command.core.CommandScope
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.SuspendingCommandScope
import com.alaeri.command.core.syncFold
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

inline fun <reified R> ICommandScopeOwner.invokeRootCommand(
    name:String?,
    commandNomenclature: CommandNomenclature = CommandNomenclature.Undefined,
    crossinline body: CommandScope<R>.()->R): R{

    val executableContext =
        ChainableCommandScope<R>(this)
    val executionContext = executableContext.chain(commandScope)
    val syncFlowBuilder = { aSyncOrSuspendExecutionContext : SuspendingCommandScope<R> -> aSyncOrSuspendExecutionContext.executeAsFlow { body.invoke(aSyncOrSuspendExecutionContext) } }
    return Command(
        this,
        chainableCommandScope = executableContext,
        executable = syncFlowBuilder
    ).syncExecute(executionContext).syncFold()
}

suspend inline fun <reified R> ICommandScopeOwner.invokeSuspendingRootCommand(
    name: String? = null,
    commandNomenclature: CommandNomenclature = CommandNomenclature.Undefined,
    crossinline body: suspend SuspendingCommandScope<R>.()->R): R{

    val executableContext =
        ChainableCommandScope<R>(this)
    val executionContext = executableContext.chain(commandScope)
    val suspendingCommand =
        SuspendingCommand<R>(
            this,
            commandNomenclature,
            name,
            chainableCommandScope = executableContext,
            executable = {
                coroutineScope {
                    println("csE: $this")
                    executionContext.execute { body.invoke(executionContext) }
                }
            }
        )
    return@invokeSuspendingRootCommand coroutineScope {
        println("csF: $this");
        val flow: Flow<CommandState<R>> = suspendingCommand.suspendExecute(executionContext)
        val retvalue = flow.syncFold<R>()
        retvalue
    }
}