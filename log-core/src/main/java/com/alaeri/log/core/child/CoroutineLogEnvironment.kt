package com.alaeri.log.core.child

import com.alaeri.log.core.LogEnvironment
import kotlin.coroutines.CoroutineContext

class CoroutineLogEnvironment(val logEnvironment: LogEnvironment) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> = CoroutineLogKey
}