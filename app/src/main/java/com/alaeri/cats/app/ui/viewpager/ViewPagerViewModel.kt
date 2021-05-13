package com.alaeri.cats.app.ui.viewpager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

data class FocusViewState(val minStart : Float,
                          val start: Float,
                          val end: Float,
                          val maxEnd: Float,
//                          val focused: IndexAndUUID?,
                          val clearFocus: ()->Unit)
@ExperimentalCoroutinesApi
class ViewPagerViewModel(
//    private val focusCommandRepository: com.alaeri.command.android.visualizer.focus.FocusCommandRepository
) : ViewModel(){
    fun onTimeRangeChanged(start: Float, end: Float) {
        viewModelScope.launch {
//            focusCommandRepository.setTimeRange(start.toLong() to end.toLong())
        }
    }

    private val mutablePages =
        MutableLiveData<List<Page>>(listOf(
            Page(id = PageId.Cats),
            Page(id = PageId.Login),
            Page(id = PageId.CommandsList),
            Page(id = PageId.CommandsLifecycle),
            Page(id = PageId.CommandsWebview)
        ))
    val pages : LiveData<List<Page>> = mutablePages
//    val focused : LiveData<FocusViewState> = focusCommandRepository.state.map {
//        it.history
//    }.filterNotNull().map {
//        it.toFocusViewState {
//            viewModelScope.launch {
//                focusCommandRepository.setFocus(null)
//            }
//        }
//    }.asLiveData(viewModelScope.coroutineContext)

}
//
//private fun com.alaeri.command.android.visualizer.focus.FocusedAndZoomCommandHistory.toFocusViewState(clearFocus: () -> Unit): FocusViewState {
//    Log.d("CATS","history: $this")
//    check(minTime <= start)
//    check(start<=end)
//    check(end<=maxTime)
//    check(minTime<maxTime)
//
//    return FocusViewState(minTime.toFloat(), start.toFloat(), end.toFloat(), maxTime.toFloat(), focus, clearFocus)
//}
