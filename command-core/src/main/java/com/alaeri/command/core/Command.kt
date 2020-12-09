package com.alaeri.command.core

import com.alaeri.command.CommandNomenclature
import com.alaeri.command.CommandState
import com.alaeri.command.core.suspend.SuspendingCommandScope
import kotlinx.coroutines.flow.*

data class Command<R>(override val owner: Any,
                      val chainableCommandScope: ChainableCommandScope<R>,
                      override val name: String? = null,
                      override val nomenclature: CommandNomenclature = CommandNomenclature.Undefined,
                      val executable: SuspendingCommandScope<R>.()-> Flow<CommandState<R>>):
    ICommand<R> {
    fun syncExecute(syncOrSuspendExecutionContext: SuspendingCommandScope<R>): Flow<CommandState<R>> = executable.invoke(syncOrSuspendExecutionContext).onEach { syncOrSuspendExecutionContext.emit(it) }
}
