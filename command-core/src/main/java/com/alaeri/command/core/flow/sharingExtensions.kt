package com.alaeri.command.core.flow

import com.alaeri.command.CommandNomenclature
import com.alaeri.command.CommandState
import com.alaeri.command.Starting
import com.alaeri.command.Value
import com.alaeri.command.core.*
import com.alaeri.command.core.suspend.SuspendingExecutionContext
import kotlinx.coroutines.flow.*

/**
 * Created by Emmanuel Requier on 04/12/2020.
 */
inline fun <reified T> FlowCommand<T>.shared() : IFlowCommand<T> {

    var upstream: Flow<T>? = null

    return object : IFlowCommand<T>{

        override fun execute(syncOrSuspendExecutionContext: SuspendingExecutionContext<T>): Flow<CommandState<T>> {
            val up = upstream
            val flowCommand = up ?: executable.invoke(syncOrSuspendExecutionContext).also { upstream = it }
            return flowCommand.map<T, CommandState<T>> {
                    Value(
                        it
                    )
                }
                .onStart { emit(Starting()) }
                .onCompletion {  }
                .onEach { syncOrSuspendExecutionContext.emit(it) }
        }

        override val owner = this@shared.owner
        override val nomenclature: CommandNomenclature =  this@shared.nomenclature
        override val name: String? =  this@shared.name
        override val executableContext: ExecutableContext<T> = this@shared.executableContext
    }
}