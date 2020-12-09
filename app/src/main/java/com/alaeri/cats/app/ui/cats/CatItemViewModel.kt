package com.alaeri.cats.app.ui.cats

import android.util.Log
import androidx.lifecycle.*
import com.alaeri.cats.app.cats.Cat
import com.alaeri.command.CommandNomenclature
import com.alaeri.command.CommandState
import com.alaeri.command.ICommandLogger
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.command.core.root.DefaultRootCommandScope
import com.alaeri.command.core.root.ICommandScopeOwner
import com.alaeri.command.core.root.buildRootCommandScope
import com.alaeri.command.core.root.invokeRootCommand
import com.alaeri.command.glide.FlowImageLoader
import com.alaeri.command.glide.ImageLoadingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CatItemViewModel(private val flowImageLoader: FlowImageLoader,
                       private val defaultSerializer: ICommandLogger): ICommandScopeOwner, ViewModel(){

    data class CatLoadingState(val imageLoadingState: ImageLoadingState)
    override val commandScope: DefaultRootCommandScope = buildRootCommandScope(
        this,
        null,
        CommandNomenclature.Root,
        defaultSerializer
    )

    private val mutableLiveDataCat =  MutableLiveData<Triple<Cat, Int, Int>?>(null)
    val catLoadingState : LiveData<CatLoadingState> = mutableLiveDataCat.switchMap { it ->
        invokeRootCommand<LiveData<CatLoadingState>>(
            name = "init CatLoadingState",
            commandNomenclature = CommandNomenclature.Application.Cats.LoadImage) {
            it?.let {
                emit(CommandState.Update(it.first))
                val cat = it.first
                val width = it.second
                val height = it.third
                val flow: Flow<com.alaeri.command.glide.ImageLoadingState> = syncInvokeFlow {
                    flowImageLoader.loadImage(cat.url, com.alaeri.command.glide.Size(width, height))
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
        Log.d("CATS", "$this onCleared()")
    }




}