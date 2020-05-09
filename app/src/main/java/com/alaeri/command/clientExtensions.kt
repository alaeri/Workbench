package com.alaeri.command

import com.alaeri.command.core.*
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.SuspendingExecutionContext
import com.alaeri.command.core.suspend.suspendFold

/**
 * Created by Emmanuel Requier on 09/05/2020.
 */
inline fun <R>  buildCommandContext(any: Any, crossinline log: IInvokationContext<R, R>.(CommandState<R>)->Unit): IInvokationContext<R, R> {
    val op = object : ICommand<R> {
        override val owner: Any = any
    }
    return object :
        IInvokationContext<R, R> {
        override val command: ICommand<R> = op
        override val invoker: Invoker<R> = object :
            Invoker<R> {
            override val owner: Any = any
        }
        override fun emit(opState: CommandState<R>) {
            this.log(opState)
        }
    }
}
suspend inline fun <reified R> Any.invokeSuspendingCommand(crossinline log: IInvokationContext<R, R>.(CommandState<R>)->Unit, crossinline body: suspend SuspendingExecutionContext<R>.()->R): R{
    return invokeSuspendingCommand(
        buildCommandContext(
            this,
            log
        ), body)
}
suspend inline fun <reified R> Any.invokeSuspendingCommand(invokationContext: IInvokationContext<R, R>, crossinline body: suspend SuspendingExecutionContext<R>.()->R): R{
    val executableContext =
        ExecutableContext<R>(this)
    val executionContext = executableContext.chain(invokationContext)
    val suspendingCommand =
        SuspendingCommand<R>(
            this,
            executableContext = executableContext,
            executable = { executionContext.execute { body.invoke(executionContext) } }
        )
    return suspendingCommand.suspendExecute(executionContext).suspendFold<R>()
}
inline fun <R> Any.invokeSyncCommand(crossinline log: IInvokationContext<R, R>.(CommandState<R>)->Unit, noinline body: ExecutionContext<R>.()->R): R{
    return invokeSyncCommand(buildCommandContext(this, log), body)
}
inline fun <R> Any.invokeSyncCommand(invokationContext: IInvokationContext<R, R>, noinline body: ExecutionContext<R>.()->R): R{
    val executableContext =
        ExecutableContext<R>(this)
    val executionContext = executableContext.chain(invokationContext)
    val syncFlowBuilder = { aSyncOrSuspendExecutionContext : SuspendingExecutionContext<R> -> aSyncOrSuspendExecutionContext.executeAsFlow { body.invoke(aSyncOrSuspendExecutionContext) } }
    return Command(
        this,
        executableContext = executableContext,
        executable = syncFlowBuilder
    ).syncExecute(executionContext).syncFold()
}
