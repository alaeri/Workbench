package com.alaeri.command.core.suspend

import com.alaeri.command.CommandState
import com.alaeri.command.android.CommandNomenclature
import com.alaeri.command.core.*
import com.alaeri.command.core.flow.FlowCommand
import com.alaeri.command.core.flow.mapUpdates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.fold

/**
 * Created by Emmanuel Requier on 09/05/2020.
 */
suspend inline fun <T,R> SuspendingExecutionContext<T>.suspendInvokeCommand(nomenclature: CommandNomenclature = CommandNomenclature.Undefined, name: String? = null, noinline body: suspend SuspendingExecutionContext<R>.()->R) : R {
    return suspendInvokeAndFold {
        this@suspendInvokeCommand.owner.suspendingCommand(name, nomenclature,body)
    }
}

//inline fun <T,reified R> SuspendingExecutionContext<T>.syncInvokeFlow(syncOperationInvoker: Any.()-> FlowCommand<R>) : Flow<R> {
//    val syncCommand = syncOperationInvoker()
//    val suspendingInvokationContext =
//        SuspendingInvokationContext2<T, R>(
//            syncCommand,
//            this
//        )
//    val executionContext = syncCommand.executableContext.chain(suspendingInvokationContext)
//    return syncCommand.execute(executionContext).mapUpdates<R,R>()
//}
suspend inline fun <T, reified R> SuspendingExecutionContext<T>.suspendInvokeFlow(suspOperationInvoker: suspend ()-> FlowCommand<R>) : Flow<R> {
    val suspendingCommand = suspOperationInvoker()
    val suspendingInvokationContext =
        SuspendingInvokationContext2<T, R>(
            suspendingCommand,
            this
        )
    val executionContext = suspendingCommand.executableContext.chain(suspendingInvokationContext)
    return suspendingCommand.execute(executionContext).mapUpdates<R,R>()
}
suspend inline fun <T,reified R> ExecutionContext<T>.suspendInvokeFlow(suspendingCommandCreator: suspend ()-> FlowCommand<R>) : Flow<R> {
    val suspendingCommand = suspendingCommandCreator.invoke()
    val syncInvokationContext2 =
        InvokationContext<T, R>(
            suspendingCommand,
            this
        )
    val executionContext = suspendingCommand.executableContext.chain(syncInvokationContext2)
    return suspendingCommand.execute(executionContext).mapUpdates<R,R>()
}
suspend inline fun <T,R, reified U> SuspendingExecutionContext<T>.suspendInvokeAsFlow(suspendingCommandCreator: ()-> SuspendingCommand<R>) : Flow<U> {
    val suspendingCommand = suspendingCommandCreator()
    val suspendingInvokationContext =
        SuspendingInvokationContext2<T, R>(
            suspendingCommand,
            this
        )
    val executionContext = suspendingCommand.executableContext.chain(suspendingInvokationContext)
    return suspendingCommand.suspendExecute(executionContext).mapUpdates()
}
inline fun <T,R, reified U> SuspendingExecutionContext<T>.syncInvokeAsFlow(syncCommandCreator: Any.()-> Command<R>) : Flow<U> {
    val syncCommand = syncCommandCreator()
    val suspendingInvokationContext =
        SuspendingInvokationContext2<T, R>(
            syncCommand,
            this
        )
    val executionContext = syncCommand.executableContext.chain(suspendingInvokationContext)
    return syncCommand.syncExecute(executionContext).mapUpdates()
}
suspend inline fun <T,R, reified U> ExecutionContext<T>.suspendInvokeAsFlow(suspendingCommandCreator: suspend ()-> SuspendingCommand<R>) : Flow<U> {
    val suspendingCommand = suspendingCommandCreator.invoke()
    val syncInvokationContext2 =
        InvokationContext<T, R>(
            suspendingCommand,
            this
        )
    val executionContext = suspendingCommand.executableContext.chain(syncInvokationContext2)
    return suspendingCommand.suspendExecute(executionContext).mapUpdates<R,U>()
}
suspend inline fun <R> Any.suspendingCommand(name: String? = null, nomenclature: CommandNomenclature = CommandNomenclature.Undefined, noinline op: suspend SuspendingExecutionContext<R>.()->R): SuspendingCommand<R> {
    val executionContext =
        ExecutableContext<R>(this)
    return SuspendingCommand(
        this,
        nomenclature,
        name,
        executionContext
    ) { this.execute { this@SuspendingCommand.op() } }
}

//suspend inline fun <T,R> SuspendingExecutionContext<T>.suspendInvokeAndFold(suspOperationInvoker: ()-> SuspendingCommand<R>) : R {
//    val suspendingCommand = suspOperationInvoker()
//    val suspendingInvokationContext =
//        SuspendingInvokationContext2<T, R>(
//            suspendingCommand,
//            this
//        )
//    val executionContext = suspendingCommand.executableContext.chain(suspendingInvokationContext)
//    return suspendingCommand.suspendExecute(executionContext).suspendFold()
//}
inline fun <T, reified R> SuspendingExecutionContext<T>.invoke(syncOperationInvoker: Any.()-> Command<R>) : R {
    val syncCommand = syncOperationInvoker()
    val suspendingInvokationContext =
        SuspendingInvokationContext2<T, R>(
            syncCommand,
            this
        )
    val executionContext = syncCommand.executableContext.chain(suspendingInvokationContext)
    return syncCommand.syncExecute(executionContext).syncFold()
}
suspend fun <ChildType> Flow<CommandState<ChildType>>.suspendFold(): ChildType{
    val lastOperationState = this@suspendFold.fold<CommandState<ChildType>, CommandState<ChildType>?>(null, { _, operationState -> operationState})
    return when(lastOperationState){
        is CommandState.Done -> lastOperationState.value
        is CommandState.Failure -> throw lastOperationState.t
        else -> throw IllegalStateException("lastOperationState is not final: $lastOperationState")
    }
}
