package com.alaeri.log.core.basic

import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.Tag
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
    private val tag : Tag = mock{}
    private val logCollector : LogCollector = mock{}
    private val basicLogEnvironmentFactory = BasicEnvironmentFactory

    //private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    @Test
    fun testBlockingBuild(){
        val build = basicLogEnvironmentFactory.blockingLogEnvironment(tag, logCollector)
        assertEquals(BasicLogEnvironment::class.java, build.javaClass)
        assertEquals(logCollector, build.collector)
        assertEquals(tag, build.tag)
    }

    @Test
    fun testSuspendingBuild()= testCoroutineScope.runBlockingTest {
        val build = basicLogEnvironmentFactory.suspendingLogEnvironment(tag, logCollector)
        assertEquals(BasicLogEnvironment::class.java, build.javaClass)
        assertEquals(logCollector, build.collector)
        assertEquals(tag, build.tag)
    }

    @Test
    fun testBlockingLog(){
        basicLogEnvironmentFactory.logBlocking(tag, logCollector) {
            verify(logCollector).emit()
        }
        verify(logCollector).emit()
    }

    @Test
    fun testBlockingException(){
        kotlin.runCatching {
            basicLogEnvironmentFactory.logBlocking(tag, logCollector) {
                verify(logCollector).emit()
                throw RuntimeException("Piou")
                @Suppress("UNREACHABLE_CODE")
                Unit
            }
        }
        verify(logCollector).emit()
    }

    @Test
    fun testSuspendingLog()= testCoroutineScope.runBlockingTest {
        val log = basicLogEnvironmentFactory.log(tag, logCollector, "params") {
            verify(logCollector).emit()
            "PIOU"
        }
        verify(logCollector).emit()
        assertEquals("PIOU", log)
    }

    @Test
    fun testSuspendingLog2()= testCoroutineScope.runBlockingTest {
        val log = basicLogEnvironmentFactory.log(tag, logCollector) {
            verify(logCollector).emit()
            "ABC"
        }
        verify(logCollector).emit()
        assertEquals("ABC", log)
    }


}