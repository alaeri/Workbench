package com.alaeri.log.extra.context.basic

import com.alaeri.log.core.context.LogContext

class ThreadLogContext(val thread: Thread = Thread.currentThread()): LogContext