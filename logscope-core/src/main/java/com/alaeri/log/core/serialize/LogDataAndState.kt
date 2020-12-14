package com.alaeri.log.core.serialize

import com.alaeri.log.core.context.LogContext
import com.alaeri.log.core.LogState

data class LogDataAndState(val logContext: LogContext, val logState: LogState)