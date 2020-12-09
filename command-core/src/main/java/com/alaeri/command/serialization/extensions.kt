package com.alaeri.command.serialization

import com.alaeri.command.CommandState
import com.alaeri.command.core.IParentCommandScope
import com.alaeri.command.serialization.entity.SerializedClass

/**
 * Created by Emmanuel Requier on 09/12/2020.
 */
internal fun Any?.toSerializedClass() = this?.let { SerializedClass(this.javaClass.name, this.javaClass.simpleName) }
internal fun spread(operationContext: IParentCommandScope<*, *>, commandState: CommandState<*>, depth: Int = 0, parentCommandContext: IParentCommandScope<*, *>) : List<CommandScopeStateDepth>{
    return when(commandState){
        is CommandState.SubCommand<*,*> -> {
            spread(commandState.subCommandAndState.first, commandState.subCommandAndState.second, depth+1, operationContext)
        }
        else -> listOf(CommandScopeStateDepth(commandState, operationContext,depth, parentCommandContext))
    }
}