package com.alaeri.cats.app.ui.cats

import androidx.paging.PagedList
import com.alaeri.cats.app.cats.Cat

sealed class PagedListState{
    open class Empty : PagedListState(){
        object AwaitingUser: Empty()
        object AwaitingCats: Empty()
    }
    data class Page(val pagedList: PagedList<Cat>, val loadMoreState: NetworkState) : PagedListState()
}