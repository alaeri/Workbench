package com.alaeri.cats.app.ui.cats

import androidx.lifecycle.*
import com.alaeri.cats.app.logBlocking
import com.alaeri.cats.app.logBlockingFlow
import com.alaeri.cats.app.logFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Created by Emmanuel Requier on 19/04/2020.
 */

data class CatFragmentState(val refreshState: NetworkState, val pagedListState: PagedListState)

class CatsViewModel(private val refreshUseCase: RefreshUseCase,
                    private val fetchMoreUserCase: PagedListUseCase
) : ViewModel(){

    private val initialState = CatFragmentState(refreshState = NetworkState.Idle(), pagedListState = PagedListState.Empty.AwaitingUser)
    private val mutableLiveDataExecutionContext = MutableLiveData<LiveData<NetworkState>>()
    private val switchMap = mutableLiveDataExecutionContext.switchMap{
        it
    }

    private val mediatorLiveData = logBlocking<MediatorLiveData<CatFragmentState>>(
        name= "build Cat list mediator live data"){

        MediatorLiveData<CatFragmentState>().apply {
            this.value = initialState
            viewModelScope.launch {

                val source = logFlow<PagedListState>("fetchMore") { fetchMoreUserCase(viewModelScope) }
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


    fun onRefreshTriggered() : Any = logBlocking<Any>(
        name = "onRefresh"
    ){
        viewModelScope.launch {
            val flow: Flow<NetworkState> = logBlockingFlow("refreshFlow"){ refreshUseCase() }
            val liveData = flow.asLiveData()
            mutableLiveDataExecutionContext.value = liveData
        }
        this
    }
}

