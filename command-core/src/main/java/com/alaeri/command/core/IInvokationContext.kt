package com.alaeri.command.core

import com.alaeri.command.CommandState

interface IInvokationContext<ParentType, in ResultType>{
    val command: ICommand<in ResultType>
    val invoker: Invoker<ParentType>
    fun emit(opState: CommandState<out ResultType>)

}