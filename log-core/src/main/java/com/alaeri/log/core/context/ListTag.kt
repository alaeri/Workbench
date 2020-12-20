package com.alaeri.log.core.context

import com.alaeri.log.core.Log

/**
 * Created by Emmanuel Requier on 14/12/2020.
 */
class ListTag(val list: List<Log.Tag>) : Log.Tag {
    override fun plus(other: Log.Tag?): Log.Tag {
        return when(other){
            is ListTag -> ListTag(list + other.list)
            is Log.Tag -> ListTag(list + other)
            else -> this
        }
    }
}