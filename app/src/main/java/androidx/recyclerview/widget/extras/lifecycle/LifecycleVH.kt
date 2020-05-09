package androidx.recyclerview.widget.extras.lifecycle

import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.lifecycle.*

abstract class LifecycleVH(itemView: View, private val parentLifecycle: Lifecycle): BindAndAttachVH(itemView),
    LifecycleOwner, LifecycleObserver {

    private val lifecycleRegistry : LifecycleRegistry = LifecycleRegistry(this).apply {
        addObserver(this@LifecycleVH)
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    override var isAttachedToWindow: Boolean = false
        set(value) {
            field = value
            handleLifecycleEvent()
        }

    override var isBound: Boolean = false
        set(value) {
            field = value
            handleLifecycleEvent()
            if(value){
                parentLifecycle.addObserver(parentLifecycleObserver)
            }else{
                parentLifecycle.removeObserver(parentLifecycleObserver)
            }
        }


    private fun handleLifecycleEvent(){
        val parentLifecycleState = parentLifecycle.currentState
        val currentState = lifecycleRegistry.currentState
        val newState = if(isAttachedToWindow && isBound){
            parentLifecycleState
        } else if(isBound){
            if(parentLifecycleState < Lifecycle.State.CREATED){
                parentLifecycleState
            } else {
                Lifecycle.State.CREATED
            }
        } else {
            if(currentState >= Lifecycle.State.CREATED) {
                Lifecycle.State.DESTROYED
            }else{
                Lifecycle.State.INITIALIZED
            }
        }
        if(currentState != newState){
            Log.d("CATS", "state: $newState from  $currentState ($parentLifecycleState with isBound: $isBound and isAttachedToWindow: $isAttachedToWindow)")
            lifecycleRegistry.currentState = newState
        }
    }

    @CallSuper
    @OnLifecycleEvent(value = Lifecycle.Event.ON_CREATE)
    open fun onCreate(){
        Log.d("CATS", "$this onCreate()")
    }

    @CallSuper
    @OnLifecycleEvent(value = Lifecycle.Event.ON_RESUME)
    open fun onResume(){
        Log.d("CATS", "$this onResume()")
    }

    @CallSuper
    @OnLifecycleEvent(value = Lifecycle.Event.ON_PAUSE)
    open fun onPause(){
        Log.d("CATS", "$this onPause()")
    }

    @CallSuper
    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    open fun onDestroy(){
        Log.d("CATS", "$this onDestroy()")
    }

    private val parentLifecycleObserver = object: LifecycleObserver {

        @OnLifecycleEvent(value = Lifecycle.Event.ON_ANY)
        fun onLifecycleEvent() {
            handleLifecycleEvent()
        }
    }
}

