package com.alaeri.command.core.flow

import com.alaeri.command.CommandNomenclature
import com.alaeri.command.CommandState
import com.alaeri.command.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

/**
 * Created by Emmanuel Requier on 09/05/2020.
 */
inline fun <T, reified R> ExecutionContext<T>.syncInvokeFlow(commandCreator: ()-> IFlowCommand<R>) : Flow<R> {
    val syncCommand = commandCreator.invoke()
    val syncInvokationContext =
        InvokationContext<T, R>(
            syncCommand,
            this
        )
    val executionContext = syncCommand.executableContext.chain(syncInvokationContext)
    return syncCommand.execute(executionContext).mapUpdates<R,R>()
}

data class Optional<R>(val value: R)
inline fun <R, reified U> Flow<CommandState<R>>.mapUpdates(): Flow<U>{
    return this.mapNotNull {
        return@mapNotNull when(it){
            is CommandState.Update<*, R> -> when(it.value){
                is U -> Optional<U>(it.value)
                else -> null
            }
            //is CommandState.Done<R> ->  Optional(it.value)
            is CommandState.Failure<R> -> throw it.t
            else -> null
        }
    }.map { it.value }
}

inline fun <T,R, reified U> ExecutionContext<T>.syncInvokeAsFlow(commandCreator: ()-> Command<R>) : Flow<U>  {
    val syncCommand = commandCreator.invoke()
    val syncInvokationContext =
        InvokationContext<T, R>(
            syncCommand,
            this
        )
    val executionContext = syncCommand.executableContext.chain(syncInvokationContext)
    return syncCommand.syncExecute(executionContext).mapUpdates()
}
inline fun <R> Any.flowCommand(name:String? = null, nomenclature: CommandNomenclature = CommandNomenclature.Undefined, crossinline op: ExecutionContext<R>.()->Flow<R>): FlowCommand<R> {
    val executionContext =
        ExecutableContext<R>(this)
    return FlowCommand<R>(
        this,
        nomenclature,
        name,
        executionContext
    ) { this@FlowCommand.op() }
}