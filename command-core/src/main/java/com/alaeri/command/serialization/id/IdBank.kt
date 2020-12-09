package com.alaeri.command.serialization.id

import com.alaeri.command.serialization.utils.WeakIdentityHashMap
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by Emmanuel Requier on 28/04/2020.
 */
//https://stackoverflow.com/questions/909843/how-to-get-the-unique-id-of-an-object-which-overrides-hashcode
open class IdBank<Key>(
    private val startingIndex : Key?,
    private val nextKey: (lastKey: Key?)->Key) {

    private val map = WeakIdentityHashMap<Any, Key>()

    class Listener(val callable: ()->Unit)
    private val listeners = mutableListOf<Listener>()
    private var lastKey: Key? = startingIndex
        private set(value){
            field = value
            listeners.forEach { it.callable() }
        }

    init {
        keyOf(this)
    }

    val lastIndexFlow : Flow<Key?> = flow {
        val channel = ConflatedBroadcastChannel<Unit>()
        val listener =
            Listener { channel.offer(Unit) }
        listeners.add(listener)
        channel.offer(Unit)
        try{
            for(signal in channel.openSubscription()){
                emit(lastKey)
            }
        }finally {
            channel.close()
            listeners.remove(listener)
        }
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
