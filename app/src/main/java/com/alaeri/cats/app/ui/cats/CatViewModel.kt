package com.alaeri.cats.app.ui.cats

import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.*
import com.alaeri.cats.app.cats.Cat
import com.alaeri.ui.glide.FlowImageLoader
import kotlinx.coroutines.launch
import org.koin.ext.getScopeName

class CatViewModel(private val flowImageLoader: FlowImageLoader): ViewModel(){

    data class CatLoadingState(val imageLoadingState: FlowImageLoader.ImageLoadingState)
    private val initialState = CatLoadingState(imageLoadingState = FlowImageLoader.ImageLoadingState.AwaitingLoad)
    private val mediatorLiveData =
        MediatorLiveData<CatLoadingState>()

    val catLoadingState : LiveData<CatLoadingState> = mediatorLiveData//MutableLiveData(CatLoadingState(FlowImageLoader.ImageLoadingState.Loading(100, 2000)))//
    private val sources = mutableListOf<LiveData<FlowImageLoader.ImageLoadingState>>()
    private lateinit var cat: Cat

    fun onItemSet(cat: Cat, width: Int, height: Int){
        //resetSources()
        this.cat = cat
        loadCat(cat, width, height)
        viewModelScope.launch {  }
    }

    private fun loadCat(cat: Cat, width: Int, height: Int) {
        Log.d("CATS","$this loadCat: ${cat.url}")
        val loadingLiveData = flowImageLoader.loadImage(cat.url, FlowImageLoader.Size(width, height)).asLiveData()
        mediatorLiveData.apply {
            addSource(loadingLiveData) {
                Log.d("CATS","${this@CatViewModel} $loadingLiveData loaded ${cat.url}")
                value = CatLoadingState(it)
            }
        }
        sources.add(loadingLiveData)
    }

    fun onRetryClicked(width: Int, height: Int){
        loadCat(cat, width, height)
    }

    public override fun onCleared() {
        super.onCleared()
        Log.d("CATS", "$this onCleared()")
        resetSources()
    }

    private fun resetSources() {
        mediatorLiveData.value?.imageLoadingState?.run {
            this as? FlowImageLoader.ImageLoadingState.Completed
        }?.run { bitmap as? BitmapDrawable }?.run {
            bitmap.recycle()
        }
        sources.forEach { mediatorLiveData.removeSource(it) }
        sources.clear()
        mediatorLiveData.value = initialState
    }
}