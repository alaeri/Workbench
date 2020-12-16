package com.alaeri.log.extra.basic

import com.alaeri.log.core.context.LogContext
import java.util.*

/**
 * Created by Emmanuel Requier on 15/12/2020.
 */
data class TimeMillisLogContext(val timeMillis : Long = Date().time): LogContext