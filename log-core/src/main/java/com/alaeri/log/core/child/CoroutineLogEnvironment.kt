package com.alaeri.log.core.child

import com.alaeri.log.core.LogEnvironment
import kotlin.coroutines.CoroutineContext

class CoroutineLogEnvironment(var logEnvironment: LogEnvironment) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> = CoroutineLogKey

    override fun toString(): String = "CoroutineLogEnvironment: ${logEnvironment.tag}"
}