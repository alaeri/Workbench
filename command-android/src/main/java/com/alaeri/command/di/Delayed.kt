package com.alaeri.command.di

import com.alaeri.command.GenericLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DelayedLogger<T>(coroutineScope: CoroutineScope, loggerFlow : Flow<GenericLogger<T>?>) :
    GenericLogger<T> {
    private val delayedLogs = mutableListOf<T>()
    private lateinit var logger: GenericLogger<T>

    init {
        coroutineScope.launch {
            val logger = loggerFlow.filterNotNull().first()
            delayedLogs.forEach { logger.log(it) }
            delayedLogs.clear()
            this@DelayedLogger.logger = logger
        }
    }

    override fun log(value :T) {
        if(::logger.isInitialized){
            logger.log(value)
        }else{
            delayedLogs.add(value)
        }
    }
}