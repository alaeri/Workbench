package com.alaeri.command.serialization

import com.alaeri.command.CommandState
import com.alaeri.command.core.IParentCommandScope

internal data class CommandScopeStateDepth(val state: CommandState<*>, val operationContext: IParentCommandScope<*, *>, val depth: Int, val parentCommandContext: IParentCommandScope<*, *>)