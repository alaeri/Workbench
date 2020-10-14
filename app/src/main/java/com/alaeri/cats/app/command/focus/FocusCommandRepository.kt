package com.alaeri.cats.app.command.focus

import com.alaeri.cats.app.command.CommandRepository
import com.alaeri.command.history.id.IndexAndUUID
import com.alaeri.command.history.serialization.SerializableCommandState
import com.alaeri.command.history.serialization.SerializableCommandStateAndContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

sealed class FocusedCommandOrBreak{
    data class Focused(val serializableCommandStateAndContext: SerializableCommandStateAndContext<IndexAndUUID>): FocusedCommandOrBreak()
    data class Break(val index: Int, val count: Int): FocusedCommandOrBreak()
}
data class FocusedAndZoomCommandHistory(
    val minTime: Long,
    val beforeCount: Int,
    val beforeFocusedCount: Int,
    val start: Long,
    val list: List<FocusedCommandOrBreak>,
    val end: Long,
    val afterCount: Int,
    val afterFocusedCount: Int,
    val maxTime: Long
)
data class State(val isComputing: Boolean, val history: FocusedAndZoomCommandHistory?)
@ExperimentalCoroutinesApi
class FocusCommandRepository(private val repository: CommandRepository){
    private val currentFocus = MutableStateFlow<IndexAndUUID?>(null)
    private val currentTimeRange = MutableStateFlow<Pair<Long,Long>?>(null)

    suspend fun setTimeRange(timeRange: Pair<Long, Long>?){
        currentTimeRange.value = timeRange
    }
    suspend fun setFocus(indexAndUUID: IndexAndUUID?){
        currentFocus.value = indexAndUUID
    }

    private val isComputingMutable = MutableStateFlow<Boolean>(false)

    private val historyFlow: Flow<FocusedAndZoomCommandHistory?> = combine(currentFocus, currentTimeRange){
        focus, timeRange ->
        isComputingMutable.value = true
        val initialData = FocusedAndZoomCommandHistory(
            minTime = Long.MAX_VALUE,
            beforeCount = 0,
            beforeFocusedCount = 0,
            start = timeRange?.first ?: 0,
            list = listOf(),
            end = timeRange?.second ?: System.currentTimeMillis(),
            afterCount = 0,
            afterFocusedCount = 0,
            maxTime = Long.MIN_VALUE)
        var numberOfBreaks = 0
        val history: FocusedAndZoomCommandHistory = repository.list.fold<SerializableCommandStateAndContext<IndexAndUUID>,FocusedAndZoomCommandHistory>(initialData){
                acc, comm ->
                    val time = comm.time
                    val inFocus = focus?.let { comm.isFocused(focus) } ?: true
                    val before = timeRange?.let { comm.time < timeRange.first } ?: false
                    val previousList = acc.list
                    val after = timeRange?.let { comm.time > timeRange.second } ?: false

                    if(before){
                        val beforeFocusedCount = acc.beforeFocusedCount + if(inFocus){1}else{0}
                        acc.copy(
                            minTime = acc.minTime.coerceAtMost(time),
                            beforeFocusedCount = beforeFocusedCount,
                            beforeCount = acc.beforeCount +1
                        )
                    }else if(after){
                        val afterFocusedCount = acc.afterFocusedCount + if(inFocus){1}else{0}
                        acc.copy(
                            maxTime = acc.maxTime.coerceAtLeast(time),
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
                                acc.copy(list = acc.list + FocusedCommandOrBreak.Break(numberOfBreaks, numberOfBreaks))
                            }
                        } else {
                            val list : List<FocusedCommandOrBreak> = acc.list + FocusedCommandOrBreak.Focused(comm)
                            acc.copy(list = list)
                        }


                    }

        }
        isComputingMutable.value = false
        history
    }



    val state : Flow<State> by lazy {
        combine(historyFlow, isComputingMutable){ his, isComputing ->
            State(isComputing = isComputing, history = his)
        }
    }

}

private fun <Key> SerializableCommandStateAndContext<Key>.isFocused(focus: Key): Boolean {
    return this.context.commandId == focus ||
    this.context.executionContext.id == focus ||
    (this.state is SerializableCommandState.Value && state.valueId == focus) ||
    (this.state is SerializableCommandState.Done && state.valueId == focus) ||
    ( this.context.invokationCommandId == focus ?: false )
}
