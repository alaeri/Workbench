package com.alaeri.log.extra.tag.callsite

import com.alaeri.log.core.Log.Tag

data class CallSiteTag(
    val stackTraceElements : List<StackTraceElement>
    = Thread.currentThread().stackTrace.toList().drop(2).take(3)): Tag