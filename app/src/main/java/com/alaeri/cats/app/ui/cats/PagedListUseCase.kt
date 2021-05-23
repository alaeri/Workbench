package com.alaeri.cats.app.ui.cats

import androidx.lifecycle.asFlow
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.alaeri.cats.app.LogOwner
import com.alaeri.cats.app.cats.Cat
import com.alaeri.cats.app.cats.CatRepository
import com.alaeri.cats.app.user.UserRepository
import com.alaeri.cats.app.log
import com.alaeri.cats.app.logBlockingFlow
import com.alaeri.cats.app.logFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PagedListUseCase(private val userRepository: UserRepository, private val catRepository: CatRepository): LogOwner{

    private val operationsChannel =
        ConflatedBroadcastChannel<NetworkState>(
            NetworkState.Idle()
        )


    private inline fun <T, U, V, W> Flow<T>.combine(
        flowU: Flow<U>,
        flowV: Flow<V>,
        crossinline transform: (T, U, V)-> W): Flow<W> = this
            .combine(flowU){ t, u -> t to u }
            .combine(flowV){ (t, u), v -> transform(t, u, v) }

    //We need the coroutine scope argument in order to cancel all jobs when viewmodel is cleared
    suspend operator fun invoke(coroutineScope: CoroutineScope) : Flow<PagedListState> = log("invokePagedListUseCase"){
        val paginatedCatsLiveData = catRepository.paginatedCatDataSource.toLiveData(
            pageSize = 20,
            initialLoadKey = 0,
            boundaryCallback = object : PagedList.BoundaryCallback<Cat>(){
                override fun onItemAtEndLoaded(itemAtEnd: Cat) {
                    super.onItemAtEndLoaded(itemAtEnd)
                    coroutineScope.launch {
                        fetchMoreCats()
                    }
                }
            }).asFlow()
        val userFlow = logBlockingFlow("userFlow"){ userRepository.currentUser }
        userFlow.combine(paginatedCatsLiveData, operationsChannel.asFlow()){ user, page, operation ->
            when{
                user == null -> PagedListState.Empty.AwaitingUser
                page.loadedCount == 0 -> PagedListState.Empty.AwaitingCats
                else -> PagedListState.Page(pagedList = page, loadMoreState = operation )
            }
        }
    }

    private suspend fun fetchMoreCats(): NetworkState = log("fetchMoreCats"){
        try {
            val user = log("getUser") {
                logFlow("userFlow"){ userRepository.currentUser }.take(1).first()
            }

            if (user != null) {
                catRepository.loadMore(user)
                NetworkState.Idle()
            } else {
                throw RuntimeException("no user connected")
            }
        } catch (e: Exception) {
            NetworkState.Idle(e)
        }

    }
}