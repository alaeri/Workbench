package com.alaeri.log.core.bridge

import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.context.LogContext
import com.alaeri.log.core.LogState
import com.alaeri.log.core.serialize.LogDataAndState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class LogBridge(private val emissionScope: CoroutineScope): LogCollector {

    private val mLogs = MutableSharedFlow<LogDataAndState>()
    val logs: SharedFlow<LogDataAndState> = mLogs

    override fun emit(logContext: LogContext, logState: LogState) {
        emissionScope.launch {
            mLogs.emit(LogDataAndState(logContext, logState))
        }
    }
}