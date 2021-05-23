package com.alaeri.cats.app.ui.cats

import androidx.lifecycle.*
import com.alaeri.cats.app.LogOwner
import com.alaeri.cats.app.cats.Cat
import com.alaeri.cats.app.log
import com.alaeri.cats.app.logBlocking
import com.alaeri.cats.app.logBlockingFlow
import com.alaeri.log.glide.FlowImageLoader
import com.alaeri.log.glide.ImageLoadingState
import com.alaeri.log.glide.Size
import kotlinx.coroutines.flow.*

class CatItemViewModel(private val flowImageLoader: FlowImageLoader): ViewModel(), LogOwner {

    data class CatLoadingState(val imageLoadingState: ImageLoadingState)

    private val mutableLiveDataCat =  MutableStateFlow<Triple<Cat, Int, Int>?>(null)

    val catLoadingState = logBlockingFlow("catLoadingState") {
        mutableLiveDataCat.flatMapLatest {
            return@flatMapLatest log("cat source object changed", it) {
                it?.let {
                    val cat = it.first
                    val width = it.second
                    val height = it.third
                    val flow: Flow<ImageLoadingState> =
                        logBlockingFlow("imageLoadingFlow") {
                            flowImageLoader.loadImage(cat.url, Size(width, height))
                        }
                    flow.map { CatLoadingState(it) }
                } ?: flowOf(CatLoadingState(ImageLoadingState.AwaitingLoad))
            }
        }
    }.asLiveData()

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