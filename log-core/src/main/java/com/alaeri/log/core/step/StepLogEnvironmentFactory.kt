package com.alaeri.log.core.step

import com.alaeri.log.core.LogEnvironment
import com.alaeri.log.core.LogEnvironmentFactory
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.collector.NoopCollector
import com.alaeri.log.core.context.LogContext

/**
 * Created by Emmanuel Requier on 15/12/2020.
 * TODO it might be possible to create a Debug stepper or something approaching it.
 * TODO investigate.
 *
 */
class StepLogEnvironmentFactory : LogEnvironmentFactory() {

    class WaitingThread(var shouldExit: Boolean = false): Thread(){

        fun loop(){
            sleep(1000)
            if(!shouldExit){
                loop()
            }
        }

        override fun run() {
            super.run()
            if(!shouldExit){
                loop()
            }
        }
    }

    override suspend fun suspendingLogEnvironment(
        logContext: LogContext,
        collector: LogCollector?
    ): LogEnvironment {
        return StepLogEnvironment(logContext, collector ?: NoopCollector, Thread().apply { start() })
    }

    override fun blockingLogEnvironment(
        logContext: LogContext,
        collector: LogCollector?
    ): LogEnvironment {
        return StepLogEnvironment(logContext, collector ?: NoopCollector , WaitingThread())
    }

}