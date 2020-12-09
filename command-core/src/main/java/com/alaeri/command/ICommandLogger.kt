package com.alaeri.command

import com.alaeri.command.core.IParentCommandScope

interface ICommandLogger {
    fun log(context: IParentCommandScope<*, *>, state: CommandState<*>)
}