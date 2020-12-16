package com.alaeri.log.extra.basic

import com.alaeri.log.core.context.LogContext

class CallSiteLogContext(
    val stackTraceElements : Array<StackTraceElement>
    = Thread.currentThread().stackTrace): LogContext