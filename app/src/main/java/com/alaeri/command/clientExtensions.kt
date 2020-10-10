package com.alaeri.command

import com.alaeri.command.android.CommandNomenclature
import com.alaeri.command.core.*
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.SuspendingExecutionContext
import com.alaeri.command.core.suspend.suspendFold

/**
 * Created by Emmanuel Requier on 09/05/2020.
 */
inline fun <R>  buildCommandContextA(any: Any,  name: String? = null, nomenclature: CommandNomenclature= CommandNomenclature.Undefined, crossinline log: IInvokationContext<R, R>.(CommandState<R>)->Unit): IInvokationContext<R, R> {
    val op = object : ICommand<R> {
        override val owner: Any = any
        override val nomenclature: CommandNomenclature = nomenclature
        override val name: String? = name
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
//inline fun <R>  buildCommandContext(any: Any, name: String? = null, nomenclature: CommandNomenclature= CommandNomenclature.Undefined, log: ICommandLogger<R>): IInvokationContext<R, R> {
//    val op = object : ICommand<R> {
//        override val owner: Any = any
//        override val nomenclature: CommandNomenclature = nomenclature
//        override val name: String? = name
//    }
//    return object :
//        IInvokationContext<R, R> {
//        override val command: ICommand<R> = op
//        override val invoker: Invoker<R> = object :
//            Invoker<R> {
//            override val owner: Any = any
//        }
//        override fun emit(opState: CommandState<R>) {
//            log.log(opState)
//        }
//    }
//}
suspend inline fun <reified R> Any.invokeSuspendingCommand(
    crossinline log: IInvokationContext<R, R>.(CommandState<R>)->Unit,
    name: String? = null,
    nomenclature: CommandNomenclature = CommandNomenclature.Undefined,
    crossinline body: suspend SuspendingExecutionContext<R>.()->R): R{
    return invokeSuspendingCommand(
        buildCommandContextA(
            this,
            name,
            nomenclature,
            log
        ),name, nomenclature, body)
}
suspend inline fun <reified R> Any.invokeSuspendingCommand(invokationContext: IInvokationContext<R, R>, name: String? = null,
                                                           nomenclature: CommandNomenclature = CommandNomenclature.Undefined,
                                                           crossinline body: suspend SuspendingExecutionContext<R>.()->R): R{
    val executableContext =
        ExecutableContext<R>(this)
    val executionContext = executableContext.chain(invokationContext)
    val suspendingCommand =
        SuspendingCommand<R>(
            this,
            nomenclature,
                    name,
            executableContext = executableContext,
            executable = { executionContext.execute { body.invoke(executionContext) } }
        )
    return suspendingCommand.suspendExecute(executionContext).suspendFold<R>()
}
//inline fun <R> Any.invokeSyncCommand(log: ICommandLogger<R>, name:String?, commandNomenclature: CommandNomenclature = CommandNomenclature.Undefined, noinline body: ExecutionContext<R>.()->R): R{
//    return invokeSyncCommand(buildCommandContextA(this,  name, commandNomenclature, log), body)
//}
inline fun <R> Any.invokeSyncCommand(crossinline log: IInvokationContext<R, R>.(CommandState<R>)->Unit, name:String?, commandNomenclature: CommandNomenclature = CommandNomenclature.Undefined,noinline body: ExecutionContext<R>.()->R): R{
    return invokeSyncCommand(buildCommandContextA(this, name, commandNomenclature, log), body)
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
