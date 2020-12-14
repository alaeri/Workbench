package com.alaeri.log.core.context

/**
 * LogContext allows you to collect logging metadata
 * You can create your own logContexts and simply add them
 * @see com.alaeri.log.core.context.ChildLogContext
 * @see com.alaeri.log.core.context.EmptyLogContext
 * @see com.alaeri.log.extra.basic.NamedLogContext
 *
 * They implement the "+" operator so you can combine them as: context1 + context2 + context3
 *
 */
interface LogContext {

    infix operator fun plus(other: LogContext?): LogContext {
        return when(other){
            is ListLogContext -> ListLogContext(listOf(this) + other.list)
            is LogContext -> ListLogContext(listOf(this, other))
            else -> this
        }
    }
}