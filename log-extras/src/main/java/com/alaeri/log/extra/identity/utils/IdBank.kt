package com.alaeri.log.extra.identity.utils

//https://stackoverflow.com/questions/909843/how-to-get-the-unique-id-of-an-object-which-overrides-hashcode
class IdBank<Key>(
    private val startingIndex : Key?,
    private val nextKey: (lastKey: Key?)->Key,
    private val map : MutableMap<Any, Key> = WeakIdentityHashMap<Any, Key>()){

    private var lastKey: Key? = startingIndex
        private set(value){
            field = value
        }

    init {
        keyOf(this)
    }

    fun keyOf(any: Any) : Key = map.getOrPut(any){
        val nextKey = nextKey(lastKey)
        nextKey.apply {
            lastKey = this
        }
    }

    fun clear() {
        map.clear()
    }
}
