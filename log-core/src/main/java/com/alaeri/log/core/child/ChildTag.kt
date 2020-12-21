package com.alaeri.log.core.child

import com.alaeri.log.core.Log.Tag

class ChildTag(val parentTag: Tag): Tag {


    override fun toString(): String {
        val hashCode = this.hashCode()
        val parentHashCode = parentTag.hashCode()
        return "ChildTag@$hashCode(parent=@$parentHashCode)"
    }
}