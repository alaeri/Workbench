package com.alaeri.command

import com.alaeri.command.CommandState
import com.alaeri.command.core.IInvokationContext

interface DefaultIRootCommandLogger {
    fun log(context: IInvokationContext<*, *>, state: CommandState<*>)
}