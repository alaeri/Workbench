package com.alaeri.command.core

import com.alaeri.command.CommandState

data class ParentCommandScope<ParentType, in ResultType>(
    override val command: ICommand<in ResultType>,
    private val commandScope: CommandScope<ParentType>
): IParentCommandScope<ParentType, ResultType> {
    override val invoker: Invoker<ParentType> = commandScope

    override fun emit(opState: CommandState<out ResultType>) {
        commandScope.emit(CommandState.SubCommand(this to opState))
    }
}