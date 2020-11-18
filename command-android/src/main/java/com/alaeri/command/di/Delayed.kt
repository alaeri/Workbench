package com.alaeri.command.di

import com.alaeri.command.AbstractCommandLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DelayedLogger<T>(coroutineScope: CoroutineScope, loggerFlow : Flow<AbstractCommandLogger<T>?>) :
    AbstractCommandLogger<T> {
    private val delayedLogs = mutableListOf<T>()
    private lateinit var commandLogger: AbstractCommandLogger<T>

    init {
        coroutineScope.launch {
            val logger = loggerFlow.filterNotNull().first()
            delayedLogs.forEach { logger.log(it) }
            delayedLogs.clear()
            commandLogger = logger
        }
    }

    override fun log(value :T) {
        if(::commandLogger.isInitialized){
            commandLogger.log(value)
        }else{
            delayedLogs.add(value)
        }
    }
}