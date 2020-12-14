package com.alaeri.log.core

import kotlin.coroutines.CoroutineContext

class CoroutineLogContext(val logEnvironment: LogEnvironment) :
    CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> = CoroutineLogKey
}