package com.alaeri.cats.app.ui.cats

import androidx.lifecycle.*
import com.alaeri.cats.app.cats.Cat
import com.alaeri.log.glide.FlowImageLoader
import com.alaeri.log.glide.ImageLoadingState
import com.alaeri.log.glide.Size
import com.alaeri.cats.app.logBlocking
import com.alaeri.cats.app.logBlockingFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CatItemViewModel(private val flowImageLoader: FlowImageLoader): ViewModel(){

    data class CatLoadingState(val imageLoadingState: ImageLoadingState)


    private val mutableLiveDataCat =  MutableLiveData<Triple<Cat, Int, Int>?>(null)
    val catLoadingState : LiveData<CatLoadingState> = mutableLiveDataCat.switchMap { it ->
        return@switchMap logBlocking<LiveData<CatLoadingState>>("catLoadingLiveData"){
            it?.let {
                val cat = it.first
                val width = it.second
                val height = it.third
                val flow: Flow<ImageLoadingState> = logBlockingFlow("imageLoadingFlow") {
                    flowImageLoader.loadImage(cat.url, Size(width, height))
                }
                flow.map { CatLoadingState(it) }.asLiveData()
            } ?: MutableLiveData<CatLoadingState>()
        }
    }


    fun onItemSet(cat: Cat, width: Int, height: Int) {
        //resetSources()
        mutableLiveDataCat.value = Triple(cat, width, height)
    }

    fun onRetryClicked(width: Int, height: Int){
        mutableLiveDataCat.value = mutableLiveDataCat.value
    }

    public override fun onCleared() {
        super.onCleared()
        logBlocking("onCleared()"){}
    }




}