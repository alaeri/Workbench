package com.alaeri.cats.app.ui.viewpager

import android.util.Log
import androidx.lifecycle.*
import com.alaeri.log.android.ui.focus.FocusLogRepository
import com.alaeri.log.android.ui.focus.FocusedAndZoomLogHistory
import com.alaeri.log.extra.identity.IdentityRepresentation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class FocusViewState(val minStart : Float,
                          val start: Float,
                          val end: Float,
                          val maxEnd: Float,
                          val focused: IdentityRepresentation?,
                          val clearFocus: ()->Unit)
@ExperimentalCoroutinesApi
class ViewPagerViewModel(
    private val focusLogRepository: FocusLogRepository
) : ViewModel(){
    fun onTimeRangeChanged(start: Float, end: Float) {
        viewModelScope.launch {
            focusLogRepository.setTimeRange(start.toLong() to end.toLong())
        }
    }

    private val mutablePages =
        MutableLiveData<List<Page>>(listOf(
            Page(id = PageId.Cats),
            Page(id = PageId.Login),
            Page(id = PageId.LogList),
            Page(id = PageId.LogOptions),
            Page(id = PageId.LogsWebview)
        ))
    val pages : LiveData<List<Page>> = mutablePages
    val focused : LiveData<FocusViewState> = focusLogRepository.state.map {
        it.history
    }.filterNotNull().map {
        it.toFocusViewState {
            viewModelScope.launch {
                focusLogRepository.setFocus(null)
            }
        }
    }.asLiveData(viewModelScope.coroutineContext)

}
//
private fun FocusedAndZoomLogHistory.toFocusViewState(clearFocus: () -> Unit): FocusViewState {
    Log.d("CATS","history: $this")
    check(minTime <= start)
    check(start<=end)
    check(end<=maxTime)
    check(minTime<maxTime)

    return FocusViewState(minTime.toFloat(), start.toFloat(), end.toFloat(), maxTime.toFloat(), focus, clearFocus)
}
