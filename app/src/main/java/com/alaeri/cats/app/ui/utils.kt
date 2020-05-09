package com.alaeri.cats.app.ui

import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent

/**
 * Created by Emmanuel Requier on 19/04/2020.
 */
fun <T> LiveData<T>.toSingleEvent(): LiveData<T> {
    val result = LiveEvent<T>()
    result.addSource(this) {
        result.value = it
    }
    return result
}