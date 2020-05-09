package com.alaeri.command.history.id

import java.util.*

class DefaultIdStore(firstIndex: IndexAndUUID?, nextKey: (lastKey: IndexAndUUID?) -> IndexAndUUID) : IdBank<IndexAndUUID>(firstIndex, nextKey){
    companion object{
        private var backingInstance : DefaultIdStore? = null
        val instance: IdBank<IndexAndUUID> by lazy { backingInstance
            ?:  throw IllegalStateException("The IdBank singleton is not set") }
        fun create(firstIndex: IndexAndUUID? = null,
                   nextKey: (IndexAndUUID?)-> IndexAndUUID = { previous ->
                       IndexAndUUID(index = (previous?.index?:0) +1, uuid = UUID.randomUUID()) }) : DefaultIdStore = lazy {
            if(backingInstance != null){
                throw IllegalStateException("The IdBank singleton is already set")
            }
            DefaultIdStore(firstIndex, nextKey)
                .apply { backingInstance = this }
        }.value

        fun reset() {
            backingInstance?.clear()
            backingInstance = null
        }
    }
}