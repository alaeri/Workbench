package com.alaeri.command.di

import com.alaeri.command.CommandState
import com.alaeri.command.core.ICommandLogger
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

class DelayedCommandLogger<R>(scope: CoroutineScope,
                              private val loggerFlow: Flow<ICommandLogger<R>?>
): ICommandLogger<R> {

    private val delayedLogs = mutableListOf<CommandState<R>>()
    private lateinit var commandLogger: ICommandLogger<R>

    init {
        scope.launch {
            val logger = loggerFlow.filterNotNull().first()
            delayedLogs.forEach { logger.log(it) }
            delayedLogs.clear()
            commandLogger = logger
        }
    }

    override  fun log(commandState: CommandState<R>) {
        if(::commandLogger.isInitialized){
            commandLogger.log(commandState)
        }else{
            delayedLogs.add(commandState)
        }
    }
}