package com.alaeri.log.android.ui.focus

import androidx.lifecycle.asLiveData
import com.alaeri.log.extra.identity.IdentityRepresentation


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class LogFocusViewModel constructor(private val focusCommandRepository: FocusLogRepository) : ViewModel(){

    val liveData: LiveData<FocusedState> = focusCommandRepository.state.map {
        FocusedState( isComputing = it.isComputing, list = it.history?.toVM(
            onClearRange = { viewModelScope.launch { focusCommandRepository.setTimeRange(null) } },
            onClearFocus = { viewModelScope.launch { focusCommandRepository.setFocus(null) } },
            onItemWithIdClicked = { viewModelScope.launch { focusCommandRepository.setFocus(it) } }
        ) ?: listOf())
    }.asLiveData(context = viewModelScope.coroutineContext)
}

private fun FocusedAndZoomLogHistory.toVM(
    onClearRange: () -> Unit,
    onClearFocus: () -> Unit,
    onItemWithIdClicked: (key: IdentityRepresentation) -> Unit
): List<FocusLogItemVM> {
    val before = if(beforeCount > 0){
        FocusLogItemVM.Empty.End(
            RangeEnd.Before,
            focusedCount = beforeFocusedCount,
            count = beforeCount,
            onClearRange = onClearRange
        )
    }else{
        null
    }
    val mappedList : List<FocusLogItemVM> = list.map {
        return@map when(it){
            is FocusedLogOrBreak.Break -> FocusLogItemVM.Empty.Break(
                it.index,
                it.count,
                onClearFocus
            )
            is FocusedLogOrBreak.Focused -> FocusLogItemVM.Content(
                it.serializableLog,
                onItemWithIdClicked
            )
        }
    }

    val after = if(afterCount > 0){
        FocusLogItemVM.Empty.End(
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
