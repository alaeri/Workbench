package com.alaeri.command.core

open class ChainableCommandScope<R>(val owner: Any){

    fun chain(parentCommandScope : IParentCommandScope<*, R>) = ChainedCommandScope(owner, parentCommandScope)
}