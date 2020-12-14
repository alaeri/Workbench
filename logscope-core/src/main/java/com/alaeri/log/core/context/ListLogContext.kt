package com.alaeri.log.core.context

/**
 * Created by Emmanuel Requier on 14/12/2020.
 */
class ListLogContext(val list: List<LogContext>) : LogContext{
    override fun plus(other: LogContext?): LogContext {
        return when(other){
            is ListLogContext -> ListLogContext(list + other.list)
            is LogContext -> ListLogContext(list + other)
            else -> this
        }
    }
}