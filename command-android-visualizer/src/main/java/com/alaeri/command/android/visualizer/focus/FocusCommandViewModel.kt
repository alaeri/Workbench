package com.alaeri.command.android.visualizer.focus

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.alaeri.command.serialization.id.IndexAndUUID
import com.alaeri.command.serialization.entity.SerializableCommandStateAndScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

enum class RangeEnd{
    Before,
    After
}
sealed class FocusCommandItemVM{
    sealed class Empty: FocusCommandItemVM(){
        data class End(val rangeEnd: RangeEnd, val focusedCount: Int, val count: Int, val onClearRange: ()->Unit): Empty()
        data class Break(val index: Int, val count: Int, val onClearFocus: ()->Unit) : Empty()
    }
    data class Content(
        val commandStateAndScope: SerializableCommandStateAndScope<IndexAndUUID>,
        val onItemWithIdClicked: (key : IndexAndUUID) -> Unit
    ): FocusCommandItemVM()
}
data class FocusedState(val isComputing: Boolean, val list: List<FocusCommandItemVM>)
@ExperimentalCoroutinesApi
class FocusCommandViewModel constructor(private val focusCommandRepository: FocusCommandRepository) : ViewModel(){

    val liveData: LiveData<FocusedState> = focusCommandRepository.state.map {
        FocusedState( isComputing = it.isComputing, list = it.history?.toVM(
            onClearRange = { viewModelScope.launch { focusCommandRepository.setTimeRange(null) } },
            onClearFocus = { viewModelScope.launch { focusCommandRepository.setFocus(null) } },
            onItemWithIdClicked = { viewModelScope.launch { focusCommandRepository.setFocus(it) } }
        ) ?: listOf())
    }.asLiveData(context = viewModelScope.coroutineContext)
}

private fun FocusedAndZoomCommandHistory.toVM(
    onClearRange: () -> Unit,
    onClearFocus: () -> Unit,
    onItemWithIdClicked: (key: IndexAndUUID) -> Unit
): List<FocusCommandItemVM> {
    val before = if(beforeCount > 0){
        FocusCommandItemVM.Empty.End(
            RangeEnd.Before,
            focusedCount = beforeFocusedCount,
            count = beforeCount,
            onClearRange = onClearRange
        )
    }else{
        null
    }
    val mappedList : List<FocusCommandItemVM> = list.map {
        return@map when(it){
            is FocusedCommandOrBreak.Break -> FocusCommandItemVM.Empty.Break(
                it.index,
                it.count,
                onClearFocus
            )
            is FocusedCommandOrBreak.Focused -> FocusCommandItemVM.Content(
                it.serializableCommandStateAndScope,
                onItemWithIdClicked
            )
        }
    }

    val after = if(afterCount > 0){
        FocusCommandItemVM.Empty.End(
            RangeEnd.After,
            focusedCount = afterFocusedCount,
            count = afterCount,
            onClearRange = onClearRange
        )
    }else{
        null
    }
    val listWithNullableEnds = listOf(before) + mappedList + after
    return listWithNullableEnds.filterNotNull()
}
