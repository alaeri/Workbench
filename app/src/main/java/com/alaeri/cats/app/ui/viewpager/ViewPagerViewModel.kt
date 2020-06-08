package com.alaeri.cats.app.ui.viewpager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewPagerViewModel : ViewModel(){
    private val mutablePages =
        MutableLiveData<List<Page>>(listOf(
            Page(id = PageId.Cats),
            Page(id = PageId.Login),
            Page(id = PageId.CommandsList),
            Page(id = PageId.CommandsWebview)
        ))
    val pages : LiveData<List<Page>> = mutablePages


}