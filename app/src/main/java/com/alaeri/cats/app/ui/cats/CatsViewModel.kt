package com.alaeri.cats.app.ui.cats

import androidx.lifecycle.*
import com.alaeri.command.core.IInvokationContext
import com.alaeri.command.core.suspend.suspendInvokeAsFlow
import com.alaeri.command.core.suspendInvokeAndFold
import com.alaeri.command.invokeSyncCommand
import kotlinx.coroutines.launch

/**
 * Created by Emmanuel Requier on 19/04/2020.
 */

data class CatFragmentState(val refreshState: NetworkState, val pagedListState: PagedListState)

class CatsViewModel(private val refreshUseCase: RefreshUseCase,
                    private val fetchMoreUserCase: PagedListUseCase,
                    private val operationContext: IInvokationContext<*, *>
) : ViewModel(){

    private val initialState = CatFragmentState(refreshState = NetworkState.Idle(), pagedListState = PagedListState.Empty.AwaitingUser)
    private val mediatorContext = operationContext as IInvokationContext<MediatorLiveData<CatFragmentState>, MediatorLiveData<CatFragmentState>>
    private val mutableLiveDataExecutionContext = MutableLiveData<LiveData<NetworkState>>()
    private val switchMap = mutableLiveDataExecutionContext.switchMap{
        it
    }

    private val mediatorLiveData = invokeSyncCommand<MediatorLiveData<CatFragmentState>>(mediatorContext) {
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

    private val refreshContext = operationContext as IInvokationContext<Unit, Unit>
    fun onRefreshTriggered() : Unit = invokeSyncCommand(refreshContext){
        viewModelScope.launch {
            mutableLiveDataExecutionContext.value = this@invokeSyncCommand.suspendInvokeAsFlow<Unit, NetworkState, NetworkState>{ refreshUseCase.invoke() }.asLiveData()
        }
        Unit
    }
}

