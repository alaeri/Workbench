package com.alaeri.log.core

import com.alaeri.log.core.context.ListTag

data class Log(val tag: Tag, val message: Message){

    sealed class Message{
        data class Starting(val params: List<Any?>): Message()
        data class Done<T>(val result: T): Message()
        data class Failed(val exception: Throwable?): Message()
    }

    /**
     * Tag allows you to collect logging metadata
     * You can create your own logContexts and simply add them
     * @see com.alaeri.log.core.context.ChildLogContext
     * @see com.alaeri.log.core.context.EmptyTag
     * @see com.alaeri.log.extra.basic.NamedLogContext
     *
     * They implement the "+" operator so you can combine them as: context1 + context2 + context3
     *
     */
    interface Tag {
        infix operator fun plus(other: Tag?): Tag {
            return when(other){
                is ListTag -> ListTag(listOf(this) + other.list)
                is Tag -> ListTag(listOf(this, other))
                else -> this
            }
        }
    }
}

