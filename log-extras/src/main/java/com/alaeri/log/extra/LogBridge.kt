package com.alaeri.log.extra

import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * This is a LogCollector that exposes a
 * val logs: SharedFlow<LogAndState>
 * which will emit all logs received by this instance.
 *
 * TODO: define what should happen on emissionScope cancellation
 * TODO: investigate if we need to make this internal and only expose the serialized version?
 * /!\ logContext and logState are not serialized  at this stage !
 *
 */
class LogBridge(private val emissionScope: CoroutineScope,
                replay: Int = 0,
                extraBuffer: Int = 0,
                bufferOverflow: BufferOverflow = BufferOverflow.SUSPEND):
    LogCollector {

    private val mLogs = MutableSharedFlow<Log>(replay, extraBuffer, bufferOverflow)
    val logs: SharedFlow<Log> = mLogs

    override fun emit(log: Log) {
        emissionScope.launch {
            mLogs.emit(log)
        }
    }
}