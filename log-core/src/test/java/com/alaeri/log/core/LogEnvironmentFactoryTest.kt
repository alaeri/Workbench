package com.alaeri.log.core

import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.Tag
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

/**
 * Created by Emmanuel Requier on 16/12/2020.
 * @see com.alaeri.log.core.basic.BasicEnvironmentFactoryTest
 * for a similar test
 */
class LogEnvironmentFactoryTest {

    //private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)


    val collector: LogCollector = mock {  }
    val context: Tag = mock {  }
    val logEnvironment: LogEnvironment = mock { }

    val logEnvironmentFactory = object : LogEnvironmentFactory(){
        override suspend fun suspendingLogEnvironment(
            tag: Tag,
            collector: LogCollector?
        ): LogEnvironment = logEnvironment

        override fun blockingLogEnvironment(
            tag: Tag,
            collector: LogCollector?
        ): LogEnvironment = logEnvironment
    }

    @Test
    fun testBlocking(){
        whenever(logEnvironment.collector).doReturn(collector)
        whenever(logEnvironment.tag).doReturn(context)
        val temp = logEnvironmentFactory.logBlocking{
            verify(logEnvironment).prepare()
            verify(logEnvironment).collector
            "HOT"
        }
        verify(logEnvironment).dispose()
        verify(logEnvironment, times(2)).tag
        verify(logEnvironment, times(2)).collector
        verifyNoMoreInteractions(logEnvironment)
    }

    @Test
    fun testSuspend() = testCoroutineScope.runBlockingTest{
        whenever(logEnvironment.collector).doReturn(collector)
        whenever(logEnvironment.tag).doReturn(context)
        val temp = logEnvironmentFactory.log{
            verify(logEnvironment).prepare()
            verify(logEnvironment).collector
            "HOT"
        }
        verify(logEnvironment).dispose()
        verify(logEnvironment, times(2)).tag
        verify(logEnvironment, times(2)).collector
        verify(collector, times(2)).emit()
        verifyNoMoreInteractions(logEnvironment)
    }

}