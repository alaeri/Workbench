package com.alaeri.command.android

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.lifecycleScope
import com.alaeri.command.core.root.invokeSuspendingRootCommand
import com.alaeri.command.core.suspend.SuspendingCommandScope
import kotlinx.coroutines.launch

class LifecycleCommandContext(
    private val lifecycleCommandOwner: LifecycleCommandOwner
): LifecycleObserver {

    private lateinit var _currentExecutionContext : SuspendingCommandScope<Unit>
    val currentExecutionContext: SuspendingCommandScope<Unit>
            get() = _currentExecutionContext

    init{
       this.lifecycleCommandOwner.lifecycle.addObserver(this)
       lifecycleCommandOwner.lifecycleScope.launch {
           lifecycleCommandOwner.invokeSuspendingRootCommand<Unit> {
               _currentExecutionContext = this
           }
       }
   }


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onLifecycleCreated(){
        lifecycleCommandOwner.lifecycleScope.launch {
            lifecycleCommandOwner.invokeSuspendingRootCommand<Unit>("onCreate", commandNomenclature = com.alaeri.command.CommandNomenclature.Android.Lifecycle.OnCreate){
                _currentExecutionContext = this
            }
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onLifecycleStarted(){
        lifecycleCommandOwner.lifecycleScope.launch {
            lifecycleCommandOwner.invokeSuspendingRootCommand<Unit>(
                "onStart",
                commandNomenclature = com.alaeri.command.CommandNomenclature.Android.Lifecycle.OnStart
            ) {
                _currentExecutionContext = this
            }
        }

    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onLifecycleResumed(){
        lifecycleCommandOwner.lifecycleScope.launch {
            lifecycleCommandOwner.invokeSuspendingRootCommand<Unit>(
                "onResume",
                commandNomenclature = com.alaeri.command.CommandNomenclature.Android.Lifecycle.OnResume
            ) {
                _currentExecutionContext = this
            }
        }

    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onLifecyclePaused(){
        lifecycleCommandOwner.lifecycleScope.launch {
            lifecycleCommandOwner.invokeSuspendingRootCommand<Unit>(
                "onPause",
                commandNomenclature = com.alaeri.command.CommandNomenclature.Android.Lifecycle.OnPause
            ) {
                _currentExecutionContext = this
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onLifecycleStopped(){
        lifecycleCommandOwner.lifecycleScope.launch {
            lifecycleCommandOwner.invokeSuspendingRootCommand<Unit>(
                "onStop",
                commandNomenclature = com.alaeri.command.CommandNomenclature.Android.Lifecycle.OnStop
            ) {
                _currentExecutionContext = this
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onLifecycleDestryoed(){
        lifecycleCommandOwner.lifecycleScope.launch {
            lifecycleCommandOwner.invokeSuspendingRootCommand<Unit>(
                "onDestroy",
                commandNomenclature = com.alaeri.command.CommandNomenclature.Android.Lifecycle.OnDestroy
            ) {
                _currentExecutionContext = this
            }
        }
    }
}