package com.alaeri.command.core.suspend

import com.alaeri.command.CommandState
import com.alaeri.command.core.ICommand
import com.alaeri.command.core.IParentCommandScope
import com.alaeri.command.core.Invoker

data class SuspendingParentCommandScope2<ParentType,in ResultType>(
    val suspendingCommand: ICommand<in ResultType>,
    private val suspendingExecutionContext: SuspendingCommandScope<ParentType>
): IParentCommandScope<ParentType, ResultType> {
    override val invoker: Invoker<ParentType> = suspendingExecutionContext
    override val command: ICommand<in ResultType> = suspendingCommand
    override fun emit(opState: CommandState<out ResultType>) {
        suspendingExecutionContext.emit(CommandState.SubCommand<ParentType, ResultType>(this to opState))
    }
}