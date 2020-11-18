package com.alaeri.command.android.visualizer.focus

import android.util.Log
import com.alaeri.command.android.visualizer.CommandRepository
import com.alaeri.command.history.id.IndexAndUUID
import com.alaeri.command.history.serialization.SerializableCommandState
import com.alaeri.command.history.serialization.SerializableCommandStateAndContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate

sealed class FocusedCommandOrBreak{
    data class Focused(val serializableCommandStateAndContext: SerializableCommandStateAndContext<IndexAndUUID>): FocusedCommandOrBreak()
    data class Break(val index: Int, val count: Int): FocusedCommandOrBreak()
}
data class FocusedAndZoomCommandHistory(
    val minTime: Long,
    val beforeCount: Int,
    val beforeFocusedCount: Int,
    val start: Long,
    val focus: IndexAndUUID?,
    val end: Long,
    val afterCount: Int,
    val afterFocusedCount: Int,
    val maxTime: Long,
    val list: List<FocusedCommandOrBreak>
)
data class State(val isComputing: Boolean, val history: FocusedAndZoomCommandHistory?)
@ExperimentalCoroutinesApi
class FocusCommandRepository(private val repository: CommandRepository){
    val initializationTime = System.currentTimeMillis()
    private val currentFocus = MutableStateFlow<IndexAndUUID?>(null)
    private val currentTimeRange = MutableStateFlow<Pair<Long,Long>?>(null)

    suspend fun setTimeRange(timeRange: Pair<Long, Long>?){
        currentTimeRange.value = timeRange
    }
    suspend fun setFocus(indexAndUUID: IndexAndUUID?){
        currentFocus.value = indexAndUUID
    }

    private val isComputingMutable = MutableStateFlow<Boolean>(false)

    data class Acc(
        val beforeCount: Int,
        val beforeFocusedCount: Int,
        val list: List<FocusedCommandOrBreak>,
        val afterCount: Int,
        val afterFocusedCount: Int
    )

    private val historyFlow: Flow<FocusedAndZoomCommandHistory?> = combine(currentFocus, currentTimeRange){
        focus, timeRange ->
        isComputingMutable.value = true
        val initialData = Acc(
            beforeCount = 0,
            beforeFocusedCount = 0,
            list = listOf(),
            afterCount = 0,
            afterFocusedCount = 0)
        var numberOfBreaks = 0

        data class TimeAcc(val start: Long, val end: Long)
        val timeBounds = if(repository.list.isNotEmpty()){
            repository.list.fold(TimeAcc(start = Long.MAX_VALUE, end = Long.MIN_VALUE)){ acc, it ->
                acc.copy(
                    start = acc.start.coerceAtMost(it.time),
                    end = acc.end.coerceAtLeast(it.time)
                )
            }
        }else{
            TimeAcc(start = initializationTime, end = System.currentTimeMillis())
        }
        val startOrNull = timeRange?.let { it.first.coerceAtMost(it.second) + initializationTime }?.let { it.coerceAtLeast(timeBounds.start) }
        val endOrNull = timeRange?.let { it.first.coerceAtLeast(it.second) + initializationTime }?.let { it.coerceAtLeast(timeBounds.start) }//?.let{ it.coerceAtMost(timeBounds.end) }

        val history: Acc = repository.list.fold<SerializableCommandStateAndContext<IndexAndUUID>, Acc>(initialData){
                acc, comm ->
                    val time = comm.time
                    val inFocus = focus?.let { comm.isFocused(focus) } ?: true
                    val before = startOrNull?.let { comm.time < startOrNull } ?: false
                    val previousList = acc.list
                    val after = endOrNull?.let { comm.time > endOrNull } ?: false
                    if(before){
                        val beforeFocusedCount = acc.beforeFocusedCount + if(inFocus){1}else{0}
                        acc.copy(
                            beforeFocusedCount = beforeFocusedCount,
                            beforeCount = acc.beforeCount +1
                        )
                    }else if(after){
                        val afterFocusedCount = acc.afterFocusedCount + if(inFocus){1}else{0}
                        acc.copy(
                            afterFocusedCount = afterFocusedCount,
                            afterCount = acc.afterCount +1
                        )
                    } else{
                        if(!inFocus){
                            val lastBreak = previousList.lastOrNull() as? FocusedCommandOrBreak.Break
                            if(lastBreak != null){
                                val listWithNewBreakEnd = acc.list.dropLast(1) + lastBreak.copy(count = lastBreak.count + 1)
                                acc.copy(list = listWithNewBreakEnd)
                            }else{
                                acc.copy(list = acc.list + FocusedCommandOrBreak.Break(
                                    numberOfBreaks,
                                    numberOfBreaks
                                )
                                )
                            }
                        } else {
                            val list : List<FocusedCommandOrBreak> = acc.list + FocusedCommandOrBreak.Focused(
                                comm
                            )
                            acc.copy(list = list)
                        }
                    }

        }
        isComputingMutable.value = false
        Log.d("CATS","timeBounds : ${timeBounds.start}")
        Log.d("CATS","timeBounds : ${timeBounds.end}")
        Log.d("CATS","startOrNull: $startOrNull")
        Log.d("CATS","endOrNull  : $endOrNull")
        val minTime = timeBounds.start
        val maxTime = System.currentTimeMillis() //timeBounds.end
        val start = startOrNull?.coerceAtLeast(minTime) ?: minTime
        val end = endOrNull?.coerceAtMost(maxTime) ?: maxTime
        check(start <= end)
        check(minTime < maxTime)

        FocusedAndZoomCommandHistory(
            minTime = minTime - initializationTime,
            start = start - initializationTime,
            end = end - initializationTime,
            maxTime = maxTime - initializationTime,
            beforeCount = history.beforeCount,
            beforeFocusedCount = history.beforeFocusedCount,
            focus = focus,
            list = history.list,
            afterCount = history.afterCount,
            afterFocusedCount = history.afterFocusedCount
        ).also {
            Log.d("CATS","history: $it")
        }
    }



    val state : Flow<State> by lazy {
        combine(historyFlow.conflate(), isComputingMutable){ his, isComputing ->
            State(isComputing = isComputing, history = his)
        }
    }

}

private fun <Key> SerializableCommandStateAndContext<Key>.isFocused(focus: Key): Boolean {
    val state = this.state
    return this.context.executionContext.id == focus ||
    (state is SerializableCommandState.Value && state.valueId == focus) ||
    (state is SerializableCommandState.Done && state.valueId == focus) ||
    ( this.context.invokationContext.id == focus ) ||
    ( this.context.invokationCommandId == focus ?: false )
}
