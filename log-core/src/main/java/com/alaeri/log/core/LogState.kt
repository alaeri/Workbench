package com.alaeri.log.core

sealed class LogState{
    data class Starting(val params: List<Any?>): LogState()
    data class Done<T>(val result: T): LogState()
    data class Failed(val exception: Throwable?): LogState()
}