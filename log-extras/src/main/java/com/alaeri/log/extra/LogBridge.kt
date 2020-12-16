package com.alaeri.log.extra

import com.alaeri.log.core.LogState
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.context.LogContext
import com.alaeri.log.extra.serialize.LogDataAndState
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
 * TODO: define what should happen on BufferOverflow
 * TODO: define what should happen on emissionScope cancellation
 *
 * /!\ logContext and logState at this stage are not serialized!
 *
 * TBD: make this internal and only expose the serialized version?
 */
class LogBridge(private val emissionScope: CoroutineScope, replay: Int = 0, extraBuffer: Int = 0, bufferOverflow: BufferOverflow = BufferOverflow.SUSPEND):
    LogCollector {

    private val mLogs = MutableSharedFlow<LogDataAndState>(replay, extraBuffer, bufferOverflow)
    val logs: SharedFlow<LogDataAndState> = mLogs

    override fun emit(logContext: LogContext, logState: LogState) {
        emissionScope.launch {
            mLogs.emit(LogDataAndState(logContext, logState))
        }
    }
}