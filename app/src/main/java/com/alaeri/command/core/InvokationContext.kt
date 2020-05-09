package com.alaeri.command.core

import com.alaeri.command.CommandState

data class InvokationContext<ParentType, ResultType>(
    override val command: ICommand<ResultType>,
    private val executionContext: ExecutionContext<ParentType>
): IInvokationContext<ParentType, ResultType> {
    override val invoker: Invoker<ParentType> = executionContext

    override fun emit(opState: CommandState<ResultType>) {
        executionContext.emit(CommandState.SubCommand(this to opState))
    }
}