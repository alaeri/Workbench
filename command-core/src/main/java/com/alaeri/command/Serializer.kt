package com.alaeri.command

import com.alaeri.command.core.IInvokationContext
import com.alaeri.command.history.id.IdBank
import com.alaeri.command.history.serialization.SerializableCommandStateAndContext
import com.alaeri.command.history.serialize
import com.alaeri.command.history.spread

class Serializer<Key>(private val idBank: IdBank<Key>,
                      private val delayedLogger: AbstractCommandLogger<SerializableCommandStateAndContext<Key>>
) : DefaultIRootCommandLogger {

    override fun log(context: IInvokationContext<*, *>, state: CommandState<*>){
        val flatList = spread(context, state, 0, context)
        flatList.map {
            serialize(it.parentContext, it.operationContext, it.state, it.depth) {
                idBank.keyOf(this)
            }
        }.forEach {
            delayedLogger.log(it)
        }
    }
}