package com.alaeri.log.extra.tag.thread

import com.alaeri.log.core.Log.Tag

data class ThreadTag(val thread: Thread = Thread.currentThread()): Tag