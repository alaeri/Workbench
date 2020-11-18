package com.alaeri.command.history

import com.alaeri.command.*
import com.alaeri.command.core.IInvokationContext
import com.alaeri.command.history.serialization.*

data class CommandContextAndStateAndDepth(val state: CommandState<*>, val operationContext: IInvokationContext<*, *>, val depth: Int, val parentContext: IInvokationContext<*, *>)
fun spread(operationContext: IInvokationContext<*, *>, commandState: CommandState<*>, depth: Int = 0, parentContext: IInvokationContext<*, *>) : List<CommandContextAndStateAndDepth>{
    return when(commandState){
        is CommandState.SubCommand<*,*> -> {
            spread(commandState.subCommandAndState.first, commandState.subCommandAndState.second, depth+1, operationContext)
        }
        else -> listOf(CommandContextAndStateAndDepth(commandState, operationContext,depth, parentContext))
    }
}
fun Any?.toSerializedClass() = this?.let { SerializedClass(this.javaClass.name, this.javaClass.simpleName) }

fun <Key> serialize(parentContext: IInvokationContext<*, *>,
                    operationContext: IInvokationContext<*, *>,
                    commandState: CommandState<*>,
                    depth: Int,
                    keyOf: Any.()->Key) : SerializableCommandStateAndContext<Key> {

    val parentCommandId = parentContext.command.keyOf()
    val commandId = operationContext.command.keyOf()

    val serializableInvokationContext = operationContext.invoker.run {
        SerializableInvokationContext<Key>(owner.keyOf(), owner.toSerializedClass()!!, null,null, null)
    }
    val serializableExecutionContext = operationContext.command.run {
        SerializableInvokationContext<Key>(owner.keyOf(), owner.toSerializedClass()!!, null, null, null)
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


    val serializableCommandContext = SerializableCommandContext<Key>(
        depth = depth,
        commandName = operationContext.command.name,
        commandId = commandId,
        executionContext = serializableExecutionContext,
        invokationCommandId = parentCommandId,
        invokationContext = serializableInvokationContext,
        commandNomenclature = operationContext.command.nomenclature
        )
    return SerializableCommandStateAndContext<Key>(context = serializableCommandContext, state = serializableState, time= System.currentTimeMillis())
}
//sealed class FocusedStateAndContext<Key>{
//    data class Value<Key>(val commandContext: SerializableCommandContext<Key>) : FocusedStateAndContext<Key>()
//    data class Done<Key>(val commandContext: SerializableCommandContext<Key>) : FocusedStateAndContext<Key>()
//    data class Failure<Key>(val commandContext: SerializableCommandContext<Key>) : FocusedStateAndContext<Key>()
//    data class Receiver<Key>(val commandContext: SerializableCommandContext<Key>, val state: SerializableCommandState<Key>) : FocusedStateAndContext<Key>()
//    data class Invoker<Key>(val commandContext: SerializableCommandContext<Key>, val state: SerializableCommandState<Key>) : FocusedStateAndContext<Key>()
//    data class Command<Key>(val commandContext: SerializableCommandContext<Key>, val state: SerializableCommandState<Key>) : FocusedStateAndContext<Key>()
//
//}
//fun <Key> SerializableCommandStateAndContext<Key>.withFocus(any: Any): List<FocusedStateAndContext<Key>> {
//    val id = any.defaultKey()
//    val roles = mutableListOf<FocusedStateAndContext<Key>>()
//    if(this.state is SerializableCommandState.Done<*> && this.state.valueId  == id){
//        roles.add(FocusedStateAndContext.Done(this.context))
//    }
//    if(this.state is SerializableCommandState.Value<*> && this.state.valueId  == id){
//        roles.add(FocusedStateAndContext.Value(this.context))
//    }
//    if(this.state is SerializableCommandState.Failure<*> && this.state.throwableId == id){
//        roles.add(FocusedStateAndContext.Failure(this.context))
//    }
//    if(this.context.executionContext.id == id){
//        roles.add(FocusedStateAndContext.Receiver(this.context, this.state))
//    }
//    if(this.context.invokationContext.id == id){
//        roles.add(FocusedStateAndContext.Invoker(this.context, this.state))
//    }
//    if(this.context.commandId == id){
//        roles.add(FocusedStateAndContext.Command(this.context, this.state))
//    }
//    return roles
//
//}




