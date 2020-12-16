package com.alaeri.log.core.child

import com.alaeri.log.core.child.CoroutineLogEnvironment
import kotlin.coroutines.CoroutineContext

object CoroutineLogKey: CoroutineContext.Key<CoroutineLogEnvironment>