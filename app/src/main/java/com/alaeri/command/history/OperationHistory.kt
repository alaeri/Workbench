package com.alaeri.command.history

import com.alaeri.command.*
import com.alaeri.command.core.IInvokationContext
import defaultKey

data class CommandContextAndStateAndDepth(val state: CommandState<*>, val operationContext: IInvokationContext<*, *>, val depth: Int)
fun spread(operationContext: IInvokationContext<*, *>, commandState: CommandState<*>, depth: Int = 0) : List<CommandContextAndStateAndDepth>{
    return when(commandState){
        is CommandState.SubCommand<*,*> -> {
            spread(commandState.subCommandAndState.first, commandState.subCommandAndState.second, depth+1)
        }
        else -> listOf(CommandContextAndStateAndDepth(commandState, operationContext,depth))
    }
}
data class SerializableInvokationContext<Key>(
    val id: Key,
    val serializedClass: SerializedClass,
    val parentExecutionId: String?,
    val coroutineContextId: String?,
    val invokationThreadId: String?){
    override fun toString(): String {
        return "$id-${serializedClass.toString()}"
    }
}

data class SerializableCommandContext<Key>(
    val commandId: Key,
    val invokationCommandId: Key?,
    val invokationContext: SerializableInvokationContext<Key>,
    val executionContext: SerializableInvokationContext<Key>,
    val depth: Int
){
    override fun toString(): String {
        return "${commandId.toString()} invokation:${invokationContext} execution:${executionContext}"
    }
}

data class SerializedClass(val className: String, val simpleName: String){
    override fun toString(): String {
        return simpleName
    }
}
fun Any?.toSerializedClass() = this?.let { SerializedClass(this.javaClass.name, this.javaClass.simpleName) }
interface IdOwner<Key>{
    val id: Key?
    val clazz: SerializedClass?
}
sealed class SerializableCommandState<Key>{
    data class Value<Key>(val valueId: Key, override val clazz: SerializedClass?, val description: String?): SerializableCommandState<Key>(), IdOwner<Key>{
        override val id = valueId
    }
    data class Done<Key>(val valueId: Key?, override val clazz: SerializedClass?, val description: String?) : SerializableCommandState<Key>(), IdOwner<Key>{
        override val id: Key? = valueId
    }
    data class Failure<Key>(val throwableId: Key, val throwableClass: SerializedClass?, val message: String?): SerializableCommandState<Key>(), IdOwner<Key>{
        override val id: Key = throwableId
        override val clazz: SerializedClass? = throwableClass
    }
    class Waiting<Key>: SerializableCommandState<Key>()
    class Starting<Key>: SerializableCommandState<Key>()
    data class Step<Key>(val name: String? = null): SerializableCommandState<Key>()
    data class Progress<Key>(val current: Number, val max: Number): SerializableCommandState<Key>()
}
data class SerializableCommandStateAndContext<Key>(
    val context: SerializableCommandContext<Key>,
    val state: SerializableCommandState<Key>)

fun <Key> serialize(operationContext: IInvokationContext<*, *>,
                    commandState: CommandState<*>,
                    depth: Int,
                    keyOf: Any.()->Key) : SerializableCommandStateAndContext<Key> {

    val serializableState : SerializableCommandState<Key> = when(commandState){
        is CommandState.Update<*,*> -> commandState.value?.run {
            SerializableCommandState.Value<Key>(
                this.keyOf(),
                this.toSerializedClass(),
                this.toString()
            )
        } ?: SerializableCommandState.Done<Key>(Unit.keyOf(), null, null) as SerializableCommandState<Key>

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

    val serializableInvokationContext = operationContext.invoker.run {
        SerializableInvokationContext<Key>(owner.keyOf(), owner.toSerializedClass()!!, null,null, null)
    }
    val serializableExecutionContext = operationContext.command.run {
        SerializableInvokationContext<Key>(owner.keyOf(), owner.toSerializedClass()!!, null, null, null)
    }
    val serializableCommandContext = SerializableCommandContext<Key>(
        depth = depth,
        commandId = operationContext.command.keyOf(),
        executionContext = serializableExecutionContext,
        invokationCommandId = null,
        invokationContext = serializableInvokationContext
        )
    return SerializableCommandStateAndContext<Key>(context = serializableCommandContext, state = serializableState)
}
sealed class FocusedStateAndContext<Key>{
    data class Value<Key>(val commandContext: SerializableCommandContext<Key>) : FocusedStateAndContext<Key>()
    data class Done<Key>(val commandContext: SerializableCommandContext<Key>) : FocusedStateAndContext<Key>()
    data class Failure<Key>(val commandContext: SerializableCommandContext<Key>) : FocusedStateAndContext<Key>()
    data class Receiver<Key>(val commandContext: SerializableCommandContext<Key>, val state: SerializableCommandState<Key>) : FocusedStateAndContext<Key>()
    data class Invoker<Key>(val commandContext: SerializableCommandContext<Key>, val state: SerializableCommandState<Key>) : FocusedStateAndContext<Key>()
    data class Command<Key>(val commandContext: SerializableCommandContext<Key>, val state: SerializableCommandState<Key>) : FocusedStateAndContext<Key>()

}
fun <Key> SerializableCommandStateAndContext<Key>.withFocus(any: Any): List<FocusedStateAndContext<Key>> {
    val id = any.defaultKey()
    val roles = mutableListOf<FocusedStateAndContext<Key>>()
    if(this.state is SerializableCommandState.Done<*> && this.state.valueId  == id){
        roles.add(FocusedStateAndContext.Done(this.context))
    }
    if(this.state is SerializableCommandState.Value<*> && this.state.valueId  == id){
        roles.add(FocusedStateAndContext.Value(this.context))
    }
    if(this.state is SerializableCommandState.Failure<*> && this.state.throwableId == id){
        roles.add(FocusedStateAndContext.Failure(this.context))
    }
    if(this.context.executionContext.id == id){
        roles.add(FocusedStateAndContext.Receiver(this.context, this.state))
    }
    if(this.context.invokationContext.id == id){
        roles.add(FocusedStateAndContext.Invoker(this.context, this.state))
    }
    if(this.context.commandId == id){
        roles.add(FocusedStateAndContext.Command(this.context, this.state))
    }
    return roles

}




