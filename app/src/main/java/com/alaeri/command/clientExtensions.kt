package com.alaeri.command

import com.alaeri.cats.app.DefaultIRootCommandLogger
import com.alaeri.command.android.CommandNomenclature
import com.alaeri.command.core.*
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.SuspendingExecutionContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * Created by Emmanuel Requier on 09/05/2020.
 */
typealias ICommandRoot<R> = IInvokationContext<Nothing, R>
typealias AnyCommandRoot = ICommandRoot<Any?>

fun buildCommandRoot(any: Any,
                            name: String? = null,
                            nomenclature: CommandNomenclature= CommandNomenclature.Undefined,
                            iRootCommandLogger: DefaultIRootCommandLogger): AnyCommandRoot {

    val op = object : ICommand<Any?> {
        override val owner: Any = any
        override val nomenclature: CommandNomenclature = nomenclature
        override val name: String? = name
    }
    return object : AnyCommandRoot {
        override val command: ICommand<Any?> = op
        override val invoker: Invoker<Nothing> = object :
            Invoker<Nothing> {
            override val owner: Any = any
        }
        override fun emit(opState: CommandState<out Any?>) {
            iRootCommandLogger.log(this, opState)
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
//inline fun <R> Any.invokeSyncCommand(log: ICommandLogger<R>, name:String?, commandNomenclature: CommandNomenclature = CommandNomenclature.Undefined, noinline body: ExecutionContext<R>.()->R): R{
//    return invokeSyncCommand(buildCommandContextA(this,  name, commandNomenclature, log), body)
//}

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