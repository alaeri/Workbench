package com.alaeri.command

import com.alaeri.command.core.IInvokationContext

sealed class CommandState<R>(val time: Long = System.currentTimeMillis()){
    open class Update<out U,R>(val value: U): CommandState<R>()
    data class Failure<R>(val t: Throwable): CommandState<R>()
    data class Done<R>(val value: R): CommandState<R>()
//    open class SubOperation<R, RST>(val subCommandAndState: Pair<Operation, CommandState<RST>>): CommandState<R>(){
//        override fun toString(): String {
//            return subCommandAndState.first.name + " " + subCommandAndState.second.toString()
//        }
//    }
    data class SubCommand<R, RST>(val subCommandAndState: Pair<IInvokationContext<R, RST>, CommandState<out RST>>): CommandState<R>(){
        override fun toString(): String {
            return "SUBOP:" + subCommandAndState.first.toString() + " " + subCommandAndState.second.toString()
        }
    }
}

class Waiting<R>: CommandState.Update<Unit,R>(Unit)
class Starting<R>: CommandState.Update<Unit, R>(Unit)
data class Step<R>(val name: String? = null): CommandState.Update<String?, R>(name)
class Value<U, R>(value: U): CommandState.Update<U,R>(value)
data class Progress(val current: Number, val max: Number)