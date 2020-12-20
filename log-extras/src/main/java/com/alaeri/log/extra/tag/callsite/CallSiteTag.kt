package com.alaeri.log.extra.tag.callsite

import com.alaeri.log.core.Log.Tag

class CallSiteTag(
    val stackTraceElements : Array<StackTraceElement>
    = Thread.currentThread().stackTrace): Tag