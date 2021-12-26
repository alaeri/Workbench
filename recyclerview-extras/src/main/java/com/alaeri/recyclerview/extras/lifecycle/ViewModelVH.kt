package com.alaeri.recyclerview.extras.lifecycle

import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore

abstract class ViewModelVH(itemView: View,
                           parentLifecycle: Lifecycle)
    : LifecycleVH(itemView = itemView, parentLifecycle = parentLifecycle){

//    protected fun viewModelProvider(factory: ViewModelProvider.Factory): ViewModelProvider{
//        Log.d("CATS","viewModelProvider")
//        if(lifecycle.currentState < Lifecycle.State.CREATED){
//            throw IllegalStateException("Cannot access the viewModelProvider before " +
//                    "the vhiewholder lifecycle state is atLeast CREATED: ${lifecycle.currentState}")
//        }
//        return ViewModelProvider({ viewModelStore }, factory)
//    }

    override fun onDestroy() {
        Log.d("CATS","onDestroy")
        super.onDestroy()
//        viewModelStore.clear()
    }
}