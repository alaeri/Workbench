package com.alaeri.log.core.child

import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.collector.NoopCollector
import com.alaeri.log.core.context.EmptyTag
import com.alaeri.log.core.Tag
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withContext
import org.junit.Assert.*
import org.junit.Test

/**
 * Created by Emmanuel Requier on 16/12/2020.
 */
@ExperimentalCoroutinesApi
class ChildLogEnvironmentFactoryTest {

    private val tag : Tag = mock{}
    private val logCollector : LogCollector = mock{}
    private val childLogEnvironmentFactory = ChildLogEnvironmentFactory

    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    @Test
    fun testBlockingBuild(){
        val build = childLogEnvironmentFactory.blockingLogEnvironment(tag, logCollector)
        assertEquals(ChildLogEnvironment::class.java, build.javaClass)
        assertEquals(logCollector, build.collector)
        assertEquals(tag, build.tag)
        assertNull(childLogEnvironmentFactory.threadLocal.get())
    }

    @Test
    fun testSuspendingBuild()= testCoroutineScope.runBlockingTest {
        val build = childLogEnvironmentFactory.suspendingLogEnvironment(tag, logCollector)
        assertEquals(ChildLogEnvironment::class.java, build.javaClass)
        assertEquals(logCollector, build.collector)
        assertEquals(tag, build.tag)
    }

    @Test
    fun testBlockingLog(){
        childLogEnvironmentFactory.logBlocking(tag, logCollector) {
            verify(logCollector).emit()
            assertNotNull(childLogEnvironmentFactory.threadLocal.get())
        }
        verify(logCollector).emit()
        assertNull(childLogEnvironmentFactory.threadLocal.get())
    }

    @Test
    fun testBlockingException(){
        kotlin.runCatching {
            childLogEnvironmentFactory.logBlocking(tag, logCollector) {
                verify(logCollector).emit()
                assertNotNull(childLogEnvironmentFactory.threadLocal.get())
                throw RuntimeException("Piou")
                @Suppress("UNREACHABLE_CODE")
                Unit
            }
        }
        verify(logCollector).emit()
        assertNull(childLogEnvironmentFactory.threadLocal.get())
    }

    @Test
    fun testSuspendingLog()= testCoroutineScope.runBlockingTest {
        val log = childLogEnvironmentFactory.log(tag, logCollector, "params") {
            verify(logCollector).emit()
            "PIOU"
        }
        verify(logCollector).emit()
        assertEquals("PIOU", log)
    }

    @Test
    fun testSuspendingLog2()= testCoroutineScope.runBlockingTest {
        val log = childLogEnvironmentFactory.log(tag, logCollector) {
            verify(logCollector).emit()
            "ABC"
        }
        verify(logCollector).emit()
        assertEquals("ABC", log)
    }

    private val noopCollector : NoopCollector = spy(NoopCollector)

    @Test
    fun testBlockingThenSuspendingLog(){
        val abc = childLogEnvironmentFactory.logBlocking(tag, noopCollector) {
            verify(noopCollector).emit()
            var result: String? = null
            testCoroutineScope.runBlockingTest {
                result = childLogEnvironmentFactory.log {
                    verify(noopCollector).emit()
                    val suspendLambda : suspend () -> String = suspend { "ABC" }
                    withContext(testCoroutineDispatcher) { suspendLambda() }
                }
                verify(noopCollector).emit()
            }
            result
        }
        verify(noopCollector).emit()
        assertEquals("ABC", abc)
    }

    @Suppress("CHANGING_ARGUMENTS_EXECUTION_ORDER_FOR_NAMED_VARARGS")
    @Test
    fun testBlockingThenBlockingLog(){
        val abc = childLogEnvironmentFactory.logBlocking(tag, noopCollector) {
            verify(noopCollector).emit()
            val result = childLogEnvironmentFactory.logBlocking(
                tag = EmptyTag(),
                collector = null,
                params = arrayOf()) {
                verify(noopCollector).emit()
                val blockingLambda: () -> String = { "ABC" }
                blockingLambda()
            }
            verify(noopCollector).emit()
            result
        }
        verify(noopCollector).emit()
        assertEquals("ABC", abc)
    }

    @Test
    fun testSuspendingThenBlockingLog() = runBlockingTest{
        val abc = childLogEnvironmentFactory.log(tag, noopCollector) {
            verify(noopCollector).emit()
            val blockingLambda : () -> String = { "ABC" }
            childLogEnvironmentFactory.logBlocking {
                blockingLambda()
            }
        }
        assertEquals("ABC", abc)
    }

    @Test
    fun testSuspendingThenSuspendingLog() = runBlockingTest{
        val abc = childLogEnvironmentFactory.log(tag, noopCollector) {
            verify(noopCollector).emit()
            val suspendingLambda : suspend () -> String = suspend { "ABC" }
            childLogEnvironmentFactory.log {
                suspendingLambda()
            }
        }
        assertEquals("ABC", abc)
    }
}
