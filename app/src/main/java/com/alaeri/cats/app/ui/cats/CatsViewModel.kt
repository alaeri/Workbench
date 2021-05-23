package com.alaeri.cats.app.ui.cats

import androidx.lifecycle.*
import com.alaeri.cats.app.*
import com.alaeri.cats.app.log
import com.alaeri.cats.app.logBlocking
import com.alaeri.cats.app.logBlockingFlow
import com.alaeri.cats.app.logFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * Created by Emmanuel Requier on 19/04/2020.
 */

data class CatFragmentState(val refreshState: NetworkState, val pagedListState: PagedListState)

class CatsViewModel(private val refreshUseCase: RefreshUseCase,
                    private val fetchMoreUserCase: PagedListUseCase
) : ViewModel(), LogOwner {

    private val initialState = CatFragmentState(refreshState = NetworkState.Idle(), pagedListState = PagedListState.Empty.AwaitingUser)
    private val mutableLiveData= MutableLiveData<LiveData<NetworkState>>()
    private val switchMap = mutableLiveData.switchMap{
        it
    }

    private val mediatorLiveData = logBlocking<MediatorLiveData<CatFragmentState>>(
        name= "build Cat list mediator live data"){

        MediatorLiveData<CatFragmentState>().apply {
            this.value = initialState
            viewModelScope.launch {
                this@CatsViewModel.log("loadCatsFragmentState"){
                    val source = logFlow<PagedListState>("fetchMore") { fetchMoreUserCase(viewModelScope) }.flowOn(
                        Dispatchers.IO
                    )
                    addSource(source.asLiveData(currentCoroutineContext()+ Dispatchers.IO)) {
                        value = value!!.copy(pagedListState = it)
                    }
                    addSource(switchMap){
                        value = value?.copy(refreshState = it) ?: CatFragmentState(refreshState = NetworkState.Idle(), pagedListState = PagedListState.Empty())
                    }
                }
            }
        }
    }

    val currentState: LiveData<CatFragmentState> = mediatorLiveData


    fun onRefreshTriggered() : Any = logBlocking<Any>(
        name = "onRefresh"
    ){
        viewModelScope.launch {
            log("onRefreshTriggeredCoroutine"){
                val flow: Flow<NetworkState> = logBlockingFlow("refreshFlow"){ refreshUseCase() }
                val liveData = flow.asLiveData(currentCoroutineContext())
                mutableLiveData.value = liveData
            }
        }
        this
    }
}

