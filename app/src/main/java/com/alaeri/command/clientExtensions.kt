package com.alaeri.command

import com.alaeri.command.android.CommandNomenclature
import com.alaeri.command.core.*
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.SuspendingExecutionContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * Created by Emmanuel Requier on 09/05/2020.
 */
inline fun buildCommandRoot(any: Any, name: String? = null, nomenclature: CommandNomenclature= CommandNomenclature.Undefined, crossinline log: IInvokationContext<Any?, Any?>.(CommandState<out Any?>)->Unit): IInvokationContext<Any?, Any?> {
    val op = object : ICommand<Any?> {
        override val owner: Any = any
        override val nomenclature: CommandNomenclature = nomenclature
        override val name: String? = name
    }
    return object :
        IInvokationContext<Any?, Any?> {
        override val command: ICommand<Any?> = op
        override val invoker: Invoker<Any?> = object :
            Invoker<Any?> {
            override val owner: Any = any
        }
        override fun emit(opState: CommandState<out Any?>) {
            this.log(opState)
        }
    }
}
//inline fun <reified R: Any> buildCommandRoot(any: Any,
//                                 name: String? = null,
//                                 nomenclature: CommandNomenclature= CommandNomenclature.Undefined,
//                                 log: ICommandLogger<out R>): IInvokationContext<Nothing, R> {
//    val op = object : ICommand<R> {
//        override val owner: Any = any
//        override val nomenclature: CommandNomenclature = nomenclature
//        override val name: String? = name
//    }
//    return object :
//        IInvokationContext<Nothing, R> {
//        override val command: ICommand<R> = op
//        override val invoker: Invoker<Nothing> = object : Invoker<Nothing> { override val owner: Any = any }
//        override fun emit(opState: CommandState<out R>) { log.log(opState) }
//    }
//}
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
    crossinline log: IInvokationContext<Any?, Any?>.(CommandState<out Any?>)->Unit,
    name: String? = null,
    nomenclature: CommandNomenclature = CommandNomenclature.Undefined,
    crossinline body: suspend SuspendingExecutionContext<R>.()->R): R{
    return invokeSuspendingCommand(
        buildCommandRoot(
            this,
            name,
            nomenclature,
            log
        ),name, nomenclature, body)
}
suspend inline fun <reified R> Any.invokeSuspendingCommand(invokationContext: IInvokationContext<Any?, R>, name: String? = null,
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
            executable ={ coroutineScope { println("csE: $this");
                executionContext.execute { body.invoke(executionContext) } } }
        )
    return@invokeSuspendingCommand coroutineScope {
        println("csF: $this");
        val flow: Flow<CommandState<R>> = suspendingCommand.suspendExecute(executionContext)
        val retvalue = flow.syncFold<R>()
        retvalue
    }
}
//inline fun <R> Any.invokeSyncCommand(log: ICommandLogger<R>, name:String?, commandNomenclature: CommandNomenclature = CommandNomenclature.Undefined, noinline body: ExecutionContext<R>.()->R): R{
//    return invokeSyncCommand(buildCommandContextA(this,  name, commandNomenclature, log), body)
//}
inline fun <reified R> Any.invokeSyncCommand(crossinline log: IInvokationContext<Any?, Any?>.(CommandState<out Any?>)->Unit, name:String?, commandNomenclature: CommandNomenclature = CommandNomenclature.Undefined,noinline body: ExecutionContext<R>.()->R): R{
    return invokeSyncCommand(buildCommandRoot(this, name, commandNomenclature, log), body)
}
inline fun <reified R> Any.invokeSyncCommand(invokationContext: IInvokationContext<Any?, R>, noinline body: ExecutionContext<R>.()->R): R{
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
/*
 * Let's try a cleaner implementation for the root commands
 * Maybe this could be a CommandScope
 */
//inline fun <reified R: Any> Any.invokeRootCommand(name: String? = null,
//                                             nomenclature: CommandNomenclature= CommandNomenclature.Undefined,
//                                             log: ICommandLogger<out R>,
//                                             noinline body: ExecutionContext<R>.()->R) : R {
//    val rootContext = buildCommandRoot<R>(this, name, nomenclature, log)
//    val executableContext = ExecutableContext<R>(this)
//    val executionContext = executableContext.chain(rootContext)
//    val syncFlowBuilder = { aSyncOrSuspendExecutionContext: SuspendingExecutionContext<R> ->
//        aSyncOrSuspendExecutionContext.executeAsFlow {
//            body.invoke(aSyncOrSuspendExecutionContext)
//        }
//    }
//    return Command<R>(
//        this,
//        executableContext = executableContext,
//        executable = syncFlowBuilder
//    ).syncExecute(executionContext).syncFold()
//}