package com.alaeri.log.core.child

import com.alaeri.log.core.LogState
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.collector.NoopCollector
import com.alaeri.log.core.context.EmptyLogContext
import com.alaeri.log.core.context.ListLogContext
import com.alaeri.log.core.context.LogContext
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

    private val logContext : LogContext = mock{}
    private val logCollector : LogCollector = mock{}
    private val childLogEnvironmentFactory = ChildLogEnvironmentFactory

    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    @Test
    fun testBlockingBuild(){
        val build = childLogEnvironmentFactory.blockingLogEnvironment(logContext, logCollector)
        assertEquals(ChildLogEnvironment::class.java, build.javaClass)
        assertEquals(logCollector, build.collector)
        assertEquals(logContext, build.context)
        assertNull(childLogEnvironmentFactory.threadLocal.get())
    }

    @Test
    fun testSuspendingBuild()= testCoroutineScope.runBlockingTest {
        val build = childLogEnvironmentFactory.suspendingLogEnvironment(logContext, logCollector)
        assertEquals(ChildLogEnvironment::class.java, build.javaClass)
        assertEquals(logCollector, build.collector)
        assertEquals(logContext, build.context)
    }

    @Test
    fun testBlockingLog(){
        childLogEnvironmentFactory.logBlocking(logContext, logCollector) {
            verify(logCollector).emit(eq(logContext), eq(LogState.Starting(listOf())))
            assertNotNull(childLogEnvironmentFactory.threadLocal.get())
        }
        verify(logCollector).emit(eq(logContext), eq(LogState.Done(Unit)))
        assertNull(childLogEnvironmentFactory.threadLocal.get())
    }

    @Test
    fun testBlockingException(){
        kotlin.runCatching {
            childLogEnvironmentFactory.logBlocking(logContext, logCollector) {
                verify(logCollector).emit(eq(logContext), eq(LogState.Starting(listOf())))
                assertNotNull(childLogEnvironmentFactory.threadLocal.get())
                throw RuntimeException("Piou")
                @Suppress("UNREACHABLE_CODE")
                Unit
            }
        }
        verify(logCollector).emit(eq(logContext), argThat {
            this is LogState.Failed && this.exception?.message == "Piou"
        })
        assertNull(childLogEnvironmentFactory.threadLocal.get())
    }

    @Test
    fun testSuspendingLog()= testCoroutineScope.runBlockingTest {
        val log = childLogEnvironmentFactory.log(logContext, logCollector, "params") {
            verify(logCollector).emit(eq(logContext), eq(LogState.Starting(listOf("params"))))
            "PIOU"
        }
        verify(logCollector).emit(eq(logContext), eq(LogState.Done("PIOU")))
        assertEquals("PIOU", log)
    }

    @Test
    fun testSuspendingLog2()= testCoroutineScope.runBlockingTest {
        val log = childLogEnvironmentFactory.log(logContext, logCollector) {
            verify(logCollector).emit(eq(logContext), eq(LogState.Starting(listOf())))
            "ABC"
        }
        verify(logCollector).emit(eq(logContext), eq(LogState.Done("ABC")))
        assertEquals("ABC", log)
    }

    private val noopCollector : NoopCollector = spy(NoopCollector)

    @Test
    fun testBlockingThenSuspendingLog(){
        val abc = childLogEnvironmentFactory.logBlocking(logContext, noopCollector) {
            verify(noopCollector).emit(eq(logContext), eq(LogState.Starting(listOf())))
            var result: String? = null
            testCoroutineScope.runBlockingTest {
                result = childLogEnvironmentFactory.log {
                    verify(noopCollector).emit(
                        argThat { this is ListLogContext },
                        eq(LogState.Starting(listOf())))
                    val suspendLambda : suspend () -> String = suspend { "ABC" }
                    withContext(testCoroutineDispatcher) { suspendLambda() }
                }
                verify(noopCollector).emit(
                    argThat { this is ListLogContext },
                    eq(LogState.Done("ABC")))
            }
            result
        }
        verify(noopCollector).emit(eq(logContext), eq(LogState.Done("ABC")))
        assertEquals("ABC", abc)
    }

    @Suppress("CHANGING_ARGUMENTS_EXECUTION_ORDER_FOR_NAMED_VARARGS")
    @Test
    fun testBlockingThenBlockingLog(){
        val abc = childLogEnvironmentFactory.logBlocking(logContext, noopCollector) {
            verify(noopCollector).emit(eq(logContext), eq(LogState.Starting(listOf())))
            val result = childLogEnvironmentFactory.logBlocking(
                logContext = EmptyLogContext(),
                collector = null,
                params = arrayOf()) {
                verify(noopCollector).emit(
                    argThat { this is ListLogContext },
                    eq(LogState.Starting(listOf()))
                )
                val blockingLambda: () -> String = { "ABC" }
                blockingLambda()
            }
            verify(noopCollector).emit(
                argThat { this is ListLogContext },
                eq(LogState.Done("ABC")))
            result
        }
        verify(noopCollector).emit(eq(logContext), eq(LogState.Done("ABC")))
        assertEquals("ABC", abc)
    }

    @Test
    fun testSuspendingThenBlockingLog() = runBlockingTest{
        val abc = childLogEnvironmentFactory.log(logContext, noopCollector) {
            verify(noopCollector).emit(eq(logContext), eq(LogState.Starting(listOf())))
            val blockingLambda : () -> String = { "ABC" }
            childLogEnvironmentFactory.logBlocking {
                blockingLambda()
            }
        }
        assertEquals("ABC", abc)
    }

    @Test
    fun testSuspendingThenSuspendingLog() = runBlockingTest{
        val abc = childLogEnvironmentFactory.log(logContext, noopCollector) {
            verify(noopCollector).emit(eq(logContext), eq(LogState.Starting(listOf())))
            val suspendingLambda : suspend () -> String = suspend { "ABC" }
            childLogEnvironmentFactory.log {
                suspendingLambda()
            }
        }
        assertEquals("ABC", abc)
    }
}
