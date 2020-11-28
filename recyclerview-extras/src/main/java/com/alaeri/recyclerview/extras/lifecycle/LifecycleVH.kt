package com.alaeri.recyclerview.extras.lifecycle

import android.view.View
import androidx.annotation.CallSuper
import androidx.lifecycle.*

/**
 * This is an implementation suggestion for a ViewHolder which will have a lifecycle
 * composed from a provided parent lifecycle and the status / bound - recycled / attached-detached from window.
 * This can be useful if you want to associate a ViewModel with the ViewHolder
 */

/**
 * If you can use pagination and manage lifecycle in the viewmodel, imho do it rather than use this ugly workaround
 *
 * This implementation suffers from small issues when content is inserted into the list when the lifecycle is paused.
 * You will see RESUMED-PAUSED-RESUMED as it will be removed from window during the insertion of the other item
 *
 */
abstract class LifecycleVH(itemView: View, private val parentLifecycle: Lifecycle): BindAndAttachVH(itemView),
    LifecycleOwner, LifecycleObserver {

    private var lifecycleRegistry : LifecycleRegistry = instantiateNewLifecycleRegistry()

    private fun instantiateNewLifecycleRegistry() : LifecycleRegistry {
        return LifecycleRegistry(this).apply {
            addObserver(this@LifecycleVH)
        }
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
            Lifecycle.State.DESTROYED
        }
        if(currentState != newState){
            lifecycleRegistry.currentState = newState
            if(newState == Lifecycle.State.DESTROYED){
                lifecycleRegistry = instantiateNewLifecycleRegistry()
            }
        }
    }

    @CallSuper
    @OnLifecycleEvent(value = Lifecycle.Event.ON_CREATE)
    open fun onCreate(){}

    @CallSuper
    @OnLifecycleEvent(value = Lifecycle.Event.ON_RESUME)
    open fun onResume(){}

    @CallSuper
    @OnLifecycleEvent(value = Lifecycle.Event.ON_PAUSE)
    open fun onPause(){}

    @CallSuper
    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    open fun onDestroy(){}

    private val parentLifecycleObserver = object: LifecycleObserver {

        @Suppress("unused")
        @OnLifecycleEvent(value = Lifecycle.Event.ON_ANY)
        fun onLifecycleEvent() {
            //If we are resuming fragment and this holder was visible when it was paused,
            // we need to let the recyclerview process the changes
            // that happened when it was paused before resuming
            // (maybe this holder will be recycled rather than displayed)
            if(parentLifecycle.currentState == Lifecycle.State.RESUMED){
                itemView.post {
                    handleLifecycleEvent()
                }
            } else {
                handleLifecycleEvent()
            }

        }
    }
}

