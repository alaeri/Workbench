package com.alaeri.command.core.root

import com.alaeri.command.CommandNomenclature
import com.alaeri.command.CommandState
import com.alaeri.command.ICommandLogger
import com.alaeri.command.core.ICommand
import com.alaeri.command.core.Invoker

/**
 * Created by Emmanuel Requier on 09/12/2020.
 */
class RootCommandScope(any: Any,
                       name: String? = null,
                       nomenclature: CommandNomenclature = CommandNomenclature.Undefined,
                       private val iCommandLogger: ICommandLogger
) : DefaultRootCommandScope {

    private val op = object : ICommand<Any?> {
        override val owner: Any = any
        override val nomenclature: CommandNomenclature = nomenclature
        override val name: String? = name
    }

    override val command: ICommand<Any?> = op
    override val invoker: Invoker<Nothing> = object :
        Invoker<Nothing> {
        override val owner: Any = any
    }
    override fun emit(opState: CommandState<out Any?>) {
        iCommandLogger.log(this, opState)
    }

}