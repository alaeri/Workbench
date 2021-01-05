package com.alaeri.log.core.child

import com.alaeri.log.core.Log.Tag

class ChildTag(val parentTag: Tag): Tag {

    override fun toString(): String {
        val hashCode = System.identityHashCode(this)
        val parentHashCode = System.identityHashCode(parentTag)
        return "ChildTag@$hashCode(parent=@$parentHashCode)"
    }
}