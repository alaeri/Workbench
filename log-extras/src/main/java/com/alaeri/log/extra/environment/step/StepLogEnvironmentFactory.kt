package com.alaeri.log.extra.environment.step

import com.alaeri.log.core.LogEnvironment
import com.alaeri.log.core.LogEnvironmentFactory
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.collector.NoopCollector
import com.alaeri.log.core.Log.Tag
import com.alaeri.log.core.LogScope

/**
 * Created by Emmanuel Requier on 15/12/2020.
 * TODO it might be possible to create a Debug stepper or something approaching it.
 * TODO investigate if we can delay some results?
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
        tag: Tag,
        collector: LogCollector?
    ): LogEnvironment {
        return StepLogEnvironment(tag, collector ?: NoopCollector, Thread().apply { start() })
    }

    override fun blockingLogEnvironment(
        tag: Tag,
        collector: LogCollector?
    ): LogEnvironment {
        return StepLogEnvironment(tag, collector ?: NoopCollector , WaitingThread())
    }

    override suspend fun suspendingLogEnvironment(
        tag: Tag,
        collector: LogCollector?,
        scope: LogScope
    ): LogEnvironment {
        return StepLogEnvironment(tag, collector ?: NoopCollector , WaitingThread())
    }

    override fun blockingLogEnvironment(
        tag: Tag,
        collector: LogCollector?,
        logScope: LogScope
    ): LogEnvironment {
        return StepLogEnvironment(tag, collector ?: NoopCollector , WaitingThread())
    }
}