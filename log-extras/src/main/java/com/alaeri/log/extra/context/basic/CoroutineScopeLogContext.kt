package com.alaeri.log.extra.context.basic

import com.alaeri.log.core.context.LogContext
import kotlinx.coroutines.CoroutineScope

/**
 * Created by Emmanuel Requier on 15/12/2020.
 */
data class CoroutineScopeLogContext(val coroutineScope: CoroutineScope): LogContext