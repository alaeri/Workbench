package com.alaeri.command.core

import com.alaeri.command.CommandNomenclature
import com.alaeri.command.CommandState
import com.alaeri.command.core.suspend.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

inline fun <T, reified R> CommandScope<T>.invokeCommand(name: String? = null, nomenclature: CommandNomenclature = CommandNomenclature.Undefined, crossinline body: CommandScope<R>.()->R) : R {
    return@invokeCommand this.invoke<T, R> {
        this@invokeCommand.owner.command(name, nomenclature, body)
    }
}
inline fun <reified R> Any.command(name: String ?= null, nomenclature: CommandNomenclature = CommandNomenclature.Undefined, crossinline op: CommandScope<R>.()->R): Command<R> {
    val executionContext =
        ChainableCommandScope<R>(this)
    return Command(
        this,
        executionContext,
        name,
        nomenclature
    ) { this.executeAsFlow { this@Command.op() } }
}
inline fun <R> Any.command2(name: String ?= null, nomenclature: CommandNomenclature = CommandNomenclature.Undefined, noinline op: CommandScope<R>.()->R): Command<R> {
    val executionContext =
        ChainableCommandScope<R>(this)
    return Command(
        this,
        executionContext,
        name,
        nomenclature
    ) { this.executeAsFlow { this@Command.op() } }
}
inline fun <R> Any.command(name :String? = null, noinline op: CommandScope<R>.()->R): Command<R> {
    val executionContext =
        ChainableCommandScope<R>(this)
    return Command(
        this,
        executionContext,
        name,
        CommandNomenclature.Undefined
    ) { this.executeAsFlow { this@Command.op() } }
}
inline fun <T, reified R> CommandScope<T>.invoke(commandCreator: ()-> Command<R>) : R {
    val syncCommand = commandCreator.invoke()
    val syncInvokationContext =
        ParentCommandScope<T, R>(
            syncCommand,
            this
        )
    val executionContext = syncCommand.chainableCommandScope.chain(syncInvokationContext)
    return syncCommand.syncExecute(executionContext).syncFold<R>()
}
suspend inline fun <T, reified R> CommandScope<T>.suspendInvoke(crossinline commandCreator: suspend ()-> SuspendingCommand<R>) : R {
    val syncCommand = commandCreator.invoke()
    val syncInvokationContext =
        ParentCommandScope<T, R>(
            syncCommand,
            this
        )
    val executionContext = syncCommand.chainableCommandScope.chain(syncInvokationContext)
    return@suspendInvoke coroutineScope { syncCommand.suspendExecute(executionContext).suspendFold() }
}

inline fun <reified ChildType> Flow<CommandState<ChildType>>.syncFold(): ChildType{
    val lastOperationState = runBlocking {
        supervisorScope {
            this@syncFold.fold<CommandState<ChildType>, CommandState<ChildType>?>(null, { _, operationState -> operationState})
        }
    }
    return@syncFold when(lastOperationState){
        is CommandState.Done -> lastOperationState.value
        is CommandState.Failure -> throw lastOperationState.t
        else -> throw IllegalStateException("lastOperationState is not final: $lastOperationState")
    }
}
/**
 *
 * TODO move the functions to their classes
 * ADD currentCoroutineContext or callerCoroutineContext for blocking Functions?
 * ADD currentThread?
 */
suspend inline fun <T,R> CommandScope<T>.suspendInvokeAndFold(crossinline suspendingCommandCreator: suspend ()-> SuspendingCommand<R>) : R {
    val suspendingCommand = suspendingCommandCreator.invoke()
    val syncInvokationContext2 =
        ParentCommandScope<T, R>(
            suspendingCommand,
            this
        )
    val executionContext = suspendingCommand.chainableCommandScope.chain(syncInvokationContext2)
    return@suspendInvokeAndFold coroutineScope { println("csB: $coroutineContext"); suspendingCommand.suspendExecute(executionContext).suspendFold() }
}








