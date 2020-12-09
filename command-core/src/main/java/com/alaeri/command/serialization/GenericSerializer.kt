package com.alaeri.command.serialization

import com.alaeri.command.CommandState
import com.alaeri.command.GenericLogger
import com.alaeri.command.ICommandLogger
import com.alaeri.command.Starting
import com.alaeri.command.core.IParentCommandScope
import com.alaeri.command.serialization.entity.SerializableCommandState
import com.alaeri.command.serialization.id.IdBank
import com.alaeri.command.serialization.entity.SerializableCommandStateAndScope

class GenericSerializer<Key>(private val idBank: IdBank<Key>,
                             private val logger: GenericLogger<SerializableCommandStateAndScope<Key>>
) : ICommandLogger {

    override fun log(context: IParentCommandScope<*, *>, state: CommandState<*>){
        val flatList = spread(context, state, 0, context)
        flatList.map {
            serialize(it.parentCommandContext, it.operationContext, it.state, it.depth) {
                idBank.keyOf(this)
            }
        }.forEach {
            logger.log(it)
        }
    }




    internal fun <Key> serialize(parentCommandContext: IParentCommandScope<*, *>,
                        operationContext: IParentCommandScope<*, *>,
                        commandState: CommandState<*>,
                        depth: Int,
                        keyOf: Any.()->Key) : SerializableCommandStateAndScope<Key> {

        val parentCommandId = parentCommandContext.command.keyOf()
        val commandId = operationContext.command.keyOf()

        val serializableInvokationContext = operationContext.invoker.run {
            com.alaeri.command.serialization.entity.SerializableCommandInvokationScope<Key>(
                owner.keyOf(),
                owner.toSerializedClass()!!,
                null,
                null,
                null
            )
        }
        val serializableExecutionContext = operationContext.command.run {
            com.alaeri.command.serialization.entity.SerializableCommandInvokationScope<Key>(
                owner.keyOf(),
                owner.toSerializedClass()!!,
                null,
                null,
                null
            )
        }

        val serializableState : SerializableCommandState<Key> = when(commandState){
            is Starting<*> ->  SerializableCommandState.Starting<Key>(commandId)
            is CommandState.Update<*,*> -> commandState.value?.run {
                SerializableCommandState.Value<Key>(
                    this.keyOf(),
                    this.toSerializedClass(),
                    this.toString()
                )
            } ?: SerializableCommandState.Value<Key>(Unit.keyOf(), null, null) as SerializableCommandState<Key>

            is CommandState.Done<*> -> commandState.value?.run {
                SerializableCommandState.Done<Key>(
                    this.keyOf(),
                    this.toSerializedClass(),
                    this.toString()
                )
            } ?: SerializableCommandState.Done<Key>(null, null, null)

            is CommandState.Failure -> SerializableCommandState.Failure<Key>(
                throwableId = commandState.t.keyOf(),
                throwableClass = commandState.t.toSerializedClass(),
                message = commandState.t.message
            )
//        is Waiting -> SerializableCommandState.Waiting
//        is Starting -> SerializableCommandState.Starting
//        is CommandState.Progress -> SerializableCommandState.Progress(
//            commandState.current,
//            commandState.max)
//        is Step -> SerializableCommandState.Step(commandState.name)
            is CommandState.SubCommand<*, *>-> throw IllegalArgumentException("received: $commandState for $operationContext")
        } as SerializableCommandState


        val serializableCommandContext =
            com.alaeri.command.serialization.entity.SerializableCommandScope<Key>(
                depth = depth,
                commandName = operationContext.command.name,
                commandId = commandId,
                commandExecutionScope = serializableExecutionContext,
                invokationCommandId = parentCommandId,
                commandInvokationScope = serializableInvokationContext,
                commandNomenclature = operationContext.command.nomenclature
            )
        return SerializableCommandStateAndScope<Key>(scope = serializableCommandContext, state = serializableState, time= System.currentTimeMillis())
    }

}