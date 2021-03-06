package com.alaeri.command.di

import com.alaeri.command.ICommandLogger
import com.alaeri.command.core.IParentCommandScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * We want to build an invokation context that will receive a logger property after initialization
 *
 * It needs to store the commandsStates received before the logger property is initialized
 * And send these events and all subsequent ones once it is initialized
 *
 * TODO should commands be serialized here or later?
 * TODO should we add a max size?
 *
 */

class DelayedCommandLogger(scope: CoroutineScope,
                              private val loggerFlow: Flow<ICommandLogger?>
): ICommandLogger {

    data class RootCommandLog(val context: IParentCommandScope<*, *>, val state: com.alaeri.command.CommandState<*>)
    private val delayedLogs = mutableListOf<RootCommandLog>()
    private lateinit var commandLogger: ICommandLogger

    init {
        scope.launch {
            val logger = loggerFlow.filterNotNull().first()
            delayedLogs.forEach { logger.log(it.context, it.state) }
            delayedLogs.clear()
            commandLogger = logger
        }
    }

    override  fun log(context: IParentCommandScope<*,*>, commandState: com.alaeri.command.CommandState<*>) {
        if(::commandLogger.isInitialized){
            commandLogger.log(context, commandState)
        }else{
            delayedLogs.add(RootCommandLog(context, commandState))
        }
    }
}