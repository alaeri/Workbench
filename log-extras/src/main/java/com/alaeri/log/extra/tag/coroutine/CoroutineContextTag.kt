package com.alaeri.log.extra.tag.coroutine

import com.alaeri.log.core.Log.Tag
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * Created by Emmanuel Requier on 15/12/2020.
 */
data class CoroutineContextTag(val coroutineContext: CoroutineContext): Tag {
    val job: Job? = coroutineContext[Job]
}
