package com.alaeri.command.core

import com.alaeri.command.CommandState
import com.alaeri.command.core.suspend.SuspendingCommandScope

class ChainedCommandScope<R>(override val owner: Any, val parentCommandScope: IParentCommandScope<*, R>):
    SuspendingCommandScope<R> {

    override fun emit(commandState: CommandState<R>) {
        parentCommandScope.emit(commandState)
    }

    override fun toString(): String {
        return "chainedCaller: $owner"
    }
}
