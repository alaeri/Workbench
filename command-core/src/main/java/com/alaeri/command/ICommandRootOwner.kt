package com.alaeri.command

import com.alaeri.command.core.Command
import com.alaeri.command.core.ExecutableContext
import com.alaeri.command.core.ExecutionContext
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.SuspendingExecutionContext
import com.alaeri.command.core.syncFold
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

interface ICommandRootOwner {
    val commandRoot: AnyCommandRoot
}
inline fun <reified R> ICommandRootOwner.invokeRootCommand(
    name:String?,
    commandNomenclature: CommandNomenclature = CommandNomenclature.Undefined,
    noinline body: ExecutionContext<R>.()->R): R{

    val executableContext =
        ExecutableContext<R>(this)
    val executionContext = executableContext.chain(commandRoot)
    val syncFlowBuilder = { aSyncOrSuspendExecutionContext : SuspendingExecutionContext<R> -> aSyncOrSuspendExecutionContext.executeAsFlow { body.invoke(aSyncOrSuspendExecutionContext) } }
    return Command(
        this,
        executableContext = executableContext,
        executable = syncFlowBuilder
    ).syncExecute(executionContext).syncFold()
}

suspend inline fun <reified R> ICommandRootOwner.invokeSuspendingRootCommand(
    name: String? = null,
    commandNomenclature: CommandNomenclature = CommandNomenclature.Undefined,
    crossinline body: suspend SuspendingExecutionContext<R>.()->R): R{

    val executableContext =
        ExecutableContext<R>(this)
    val executionContext = executableContext.chain(commandRoot)
    val suspendingCommand =
        SuspendingCommand<R>(
            this,
            commandNomenclature,
            name,
            executableContext = executableContext,
            executable ={ coroutineScope { println("csE: $this")
                executionContext.execute { body.invoke(executionContext) } } }
        )
    return@invokeSuspendingRootCommand coroutineScope {
        println("csF: $this");
        val flow: Flow<CommandState<R>> = suspendingCommand.suspendExecute(executionContext)
        val retvalue = flow.syncFold<R>()
        retvalue
    }
}