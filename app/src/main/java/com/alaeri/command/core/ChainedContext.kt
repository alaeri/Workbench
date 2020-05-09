package com.alaeri.command.core

import com.alaeri.command.CommandState
import com.alaeri.command.core.suspend.SuspendingExecutionContext

class ChainedContext<R>(override val owner: Any, val invokationContext: IInvokationContext<*, R>):
    SuspendingExecutionContext<R> {

    override fun emit(commandState: CommandState<R>) {
        invokationContext.emit(commandState)
    }

    override fun toString(): String {
        return "chainedCaller: $owner"
    }
}
