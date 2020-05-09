package com.alaeri.command.core.suspend

import com.alaeri.command.CommandState
import com.alaeri.command.core.ICommand
import com.alaeri.command.core.IInvokationContext
import com.alaeri.command.core.Invoker

data class SuspendingInvokationContext2<ParentType, ResultType>(
    val suspendingCommand: ICommand<ResultType>,
    private val suspendingExecutionContext: SuspendingExecutionContext<ParentType>
): IInvokationContext<ParentType, ResultType> {
    override val invoker: Invoker<ParentType> = suspendingExecutionContext
    override val command: ICommand<ResultType> = suspendingCommand
    override fun emit(opState: CommandState<ResultType>) {
        suspendingExecutionContext.emit(CommandState.SubCommand<ParentType, ResultType>((this as IInvokationContext<ParentType, ResultType>) to opState))
    }
}