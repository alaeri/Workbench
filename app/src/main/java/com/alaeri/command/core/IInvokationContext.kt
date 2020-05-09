package com.alaeri.command.core

import com.alaeri.command.CommandState

interface IInvokationContext<ParentType, ResultType>{
    val command: ICommand<ResultType>
    val invoker: Invoker<ParentType>
    fun emit(opState: CommandState<ResultType>)

}