package com.alaeri.command.core

import com.alaeri.command.CommandState
import com.alaeri.command.android.CommandNomenclature
import com.alaeri.command.core.suspend.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

inline fun <T, reified R> ExecutionContext<T>.invokeCommand(name: String? = null, nomenclature: CommandNomenclature = CommandNomenclature.Undefined, crossinline body: ExecutionContext<R>.()->R) : R {
    return this.invoke<T, R> {
        this@invokeCommand.owner.command(name, nomenclature, body)
    }
}
inline fun <reified R> Any.command(name: String ?= null, nomenclature: CommandNomenclature = CommandNomenclature.Undefined, crossinline op: ExecutionContext<R>.()->R): Command<R> {
    val executionContext =
        ExecutableContext<R>(this)
    return Command(
        this,
        executionContext,
        name,
        nomenclature
    ) { this.executeAsFlow { this@Command.op() } }
}
inline fun <R> Any.command2(name: String ?= null, nomenclature: CommandNomenclature = CommandNomenclature.Undefined, noinline op: ExecutionContext<R>.()->R): Command<R> {
    val executionContext =
        ExecutableContext<R>(this)
    return Command(
        this,
        executionContext,
        name,
        nomenclature
    ) { this.executeAsFlow { this@Command.op() } }
}
inline fun <R> Any.command(name :String? = null, noinline op: ExecutionContext<R>.()->R): Command<R> {
    val executionContext =
        ExecutableContext<R>(this)
    return Command(
        this,
        executionContext
    ) { this.executeAsFlow { this@Command.op() } }
}
inline fun <T, reified R> ExecutionContext<T>.invoke(commandCreator: ()-> Command<R>) : R {
    val syncCommand = commandCreator.invoke()
    val syncInvokationContext =
        InvokationContext<T, R>(
            syncCommand,
            this
        )
    val executionContext = syncCommand.executableContext.chain(syncInvokationContext)
    return syncCommand.syncExecute(executionContext).syncFold<R>()
}

inline fun <reified ChildType> Flow<CommandState<ChildType>>.syncFold(): ChildType{
    val lastOperationState = runBlocking {
        this@syncFold.fold<CommandState<ChildType>, CommandState<ChildType>?>(null, { _, operationState -> operationState})
    }
    return when(lastOperationState){
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
suspend inline fun <T,R> ExecutionContext<T>.suspendInvokeAndFold(suspendingCommandCreator: ()-> SuspendingCommand<R>) : R {
    val suspendingCommand = suspendingCommandCreator.invoke()
    val syncInvokationContext2 =
        InvokationContext<T, R>(
            suspendingCommand,
            this
        )
    val executionContext = suspendingCommand.executableContext.chain(syncInvokationContext2)
    return suspendingCommand.suspendExecute(executionContext).suspendFold()
}








