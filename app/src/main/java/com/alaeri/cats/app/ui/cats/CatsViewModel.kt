package com.alaeri.cats.app.ui.cats

import androidx.lifecycle.*
import com.alaeri.cats.app.DefaultIRootCommandLogger
import com.alaeri.command.buildCommandRoot
import com.alaeri.command.core.suspend.suspendInvokeAsFlow
import com.alaeri.command.core.suspendInvokeAndFold
import com.alaeri.command.invokeSyncCommand
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Created by Emmanuel Requier on 19/04/2020.
 */

data class CatFragmentState(val refreshState: NetworkState, val pagedListState: PagedListState)

class CatsViewModel(private val refreshUseCase: RefreshUseCase,
                    private val fetchMoreUserCase: PagedListUseCase,
                    private val defaultSerializer: DefaultIRootCommandLogger
) : ViewModel(){

    private val initialState = CatFragmentState(refreshState = NetworkState.Idle(), pagedListState = PagedListState.Empty.AwaitingUser)
    private val mutableLiveDataExecutionContext = MutableLiveData<LiveData<NetworkState>>()
    private val switchMap = mutableLiveDataExecutionContext.switchMap{
        it
    }

    private val rootContext = buildCommandRoot(this){
        defaultSerializer.log(this, it)
    }

    private val mediatorLiveData = invokeSyncCommand<MediatorLiveData<CatFragmentState>>(rootContext) {
        return@invokeSyncCommand MediatorLiveData<CatFragmentState>().apply {
            this.value = initialState
            viewModelScope.launch {

                val source = suspendInvokeAndFold { fetchMoreUserCase(viewModelScope) }
                addSource(source.asLiveData()) {
                    value = value!!.copy(pagedListState = it)
                }
                addSource(switchMap){
                    value = value!!.copy(refreshState = it)
                }
            }
        }
    }

    val currentState: LiveData<CatFragmentState> = mediatorLiveData


    fun onRefreshTriggered() : Any = invokeSyncCommand<Any>(rootContext){
        viewModelScope.launch {
            val flow: Flow<NetworkState> = suspendInvokeAsFlow<Any, NetworkState, NetworkState>{ refreshUseCase() }
            val liveData = flow.asLiveData()
            mutableLiveDataExecutionContext.value = liveData
        }
        this
    }
}

