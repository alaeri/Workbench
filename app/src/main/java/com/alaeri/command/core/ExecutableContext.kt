package com.alaeri.command.core

open class ExecutableContext<R>(val owner: Any){

    fun chain(invokationContext : IInvokationContext<*, R>) = ChainedContext(owner, invokationContext)
}