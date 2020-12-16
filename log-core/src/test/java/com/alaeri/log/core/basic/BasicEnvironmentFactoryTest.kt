package com.alaeri.log.core.basic

import com.alaeri.log.core.LogState
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.context.LogContext
import com.nhaarman.mockitokotlin2.*
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import java.lang.RuntimeException

/**
 * Created by Emmanuel Requier on 16/12/2020.
 */
class BasicEnvironmentFactoryTest {
    private val logContext : LogContext = mock{}
    private val logCollector : LogCollector = mock{}
    private val basicLogEnvironmentFactory = BasicEnvironmentFactory

    //private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    @Test
    fun testBlockingBuild(){
        val build = basicLogEnvironmentFactory.blockingLogEnvironment(logContext, logCollector)
        assertEquals(BasicLogEnvironment::class.java, build.javaClass)
        assertEquals(logCollector, build.collector)
        assertEquals(logContext, build.context)
    }

    @Test
    fun testSuspendingBuild()= testCoroutineScope.runBlockingTest {
        val build = basicLogEnvironmentFactory.suspendingLogEnvironment(logContext, logCollector)
        assertEquals(BasicLogEnvironment::class.java, build.javaClass)
        assertEquals(logCollector, build.collector)
        assertEquals(logContext, build.context)
    }

    @Test
    fun testBlockingLog(){
        basicLogEnvironmentFactory.logBlocking(logContext, logCollector) {
            verify(logCollector).emit(eq(logContext), eq(LogState.Starting(listOf())))
        }
        verify(logCollector).emit(eq(logContext), eq(LogState.Done(Unit)))
    }

    @Test
    fun testBlockingException(){
        kotlin.runCatching {
            basicLogEnvironmentFactory.logBlocking(logContext, logCollector) {
                verify(logCollector).emit(eq(logContext), eq(LogState.Starting(listOf())))
                throw RuntimeException("Piou")
                @Suppress("UNREACHABLE_CODE")
                Unit
            }
        }
        verify(logCollector).emit(eq(logContext), argThat {
            this is LogState.Failed && this.exception?.message == "Piou"
        })
    }

    @Test
    fun testSuspendingLog()= testCoroutineScope.runBlockingTest {
        val log = basicLogEnvironmentFactory.log(logContext, logCollector, "params") {
            verify(logCollector).emit(eq(logContext), eq(LogState.Starting(listOf("params"))))
            "PIOU"
        }
        verify(logCollector).emit(eq(logContext), eq(LogState.Done("PIOU")))
        assertEquals("PIOU", log)
    }

    @Test
    fun testSuspendingLog2()= testCoroutineScope.runBlockingTest {
        val log = basicLogEnvironmentFactory.log(logContext, logCollector) {
            verify(logCollector).emit(eq(logContext), eq(LogState.Starting(listOf())))
            "ABC"
        }
        verify(logCollector).emit(eq(logContext), eq(LogState.Done("ABC")))
        assertEquals("ABC", log)
    }


}