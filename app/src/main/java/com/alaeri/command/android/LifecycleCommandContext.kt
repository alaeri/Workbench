package com.alaeri.command.android

import androidx.lifecycle.*
import com.alaeri.cats.app.DefaultIRootCommandLogger
import com.alaeri.command.buildCommandRoot
import com.alaeri.command.core.ExecutionContext
import com.alaeri.command.core.IInvokationContext
import com.alaeri.command.core.invokeCommand
import com.alaeri.command.core.suspend.SuspendingExecutionContext
import com.alaeri.command.core.suspend.suspendInvokeCommand
import com.alaeri.command.invokeSuspendingRootCommand
import com.alaeri.command.invokeRootCommand
import kotlinx.coroutines.launch
import org.koin.android.scope.scope

class LifecycleCommandContext(
    private val lifecycleCommandOwner: LifecycleCommandOwner
): LifecycleObserver {

   init{
       this.lifecycleCommandOwner.lifecycle.addObserver(this)
   }

    var currentExecutionContext : SuspendingExecutionContext<Unit>? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onLifecycleCreated(){
        lifecycleCommandOwner.lifecycleScope.launch {
            lifecycleCommandOwner.invokeSuspendingRootCommand<Unit>("onCreate", commandNomenclature = CommandNomenclature.Android.Lifecycle.OnCreate){
                currentExecutionContext = this
            }
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onLifecycleStarted(){
        lifecycleCommandOwner.lifecycleScope.launch {
            lifecycleCommandOwner.invokeSuspendingRootCommand<Unit>(
                "onStart",
                commandNomenclature = CommandNomenclature.Android.Lifecycle.OnStart
            ) {
                currentExecutionContext = this
            }
        }

    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onLifecycleResumed(){
        lifecycleCommandOwner.lifecycleScope.launch {
            lifecycleCommandOwner.invokeSuspendingRootCommand<Unit>(
                "onResume",
                commandNomenclature = CommandNomenclature.Android.Lifecycle.OnResume
            ) {
                currentExecutionContext = this
            }
        }

    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onLifecyclePaused(){
        lifecycleCommandOwner.lifecycleScope.launch {
            lifecycleCommandOwner.invokeSuspendingRootCommand<Unit>(
                "onPause",
                commandNomenclature = CommandNomenclature.Android.Lifecycle.OnPause
            ) {
                currentExecutionContext = this
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onLifecycleStopped(){
        lifecycleCommandOwner.lifecycleScope.launch {
            lifecycleCommandOwner.invokeSuspendingRootCommand<Unit>(
                "onStop",
                commandNomenclature = CommandNomenclature.Android.Lifecycle.OnStop
            ) {
                currentExecutionContext = this
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onLifecycleDestryoed(){
        lifecycleCommandOwner.lifecycleScope.launch {
            lifecycleCommandOwner.invokeSuspendingRootCommand<Unit>(
                "onDestroy",
                commandNomenclature = CommandNomenclature.Android.Lifecycle.OnDestroy
            ) {
                currentExecutionContext = this
            }
        }
    }
}