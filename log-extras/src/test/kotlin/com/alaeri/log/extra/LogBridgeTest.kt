package com.alaeri.log.extra

import com.alaeri.log.core.Data
import com.alaeri.log.core.Log
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by Emmanuel Requier on 15/12/2020.
 */
class LogBridgeTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private val collector: FlowCollector<Log> = mock {  }
    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun testLogBridge() : Unit = runBlocking {

        val logBridge = LogBridge(this)
        val collectionJob = launch {
            logBridge.logs.collect { collector.emit(it) }
        }
        logBridge.emit()
        delay(100)
        verify(collector).emit(any())
        collectionJob.cancel()
    }

    @Test
    fun testLogBridgeInTestCoroutineScope() : Unit = testCoroutineScope.runBlockingTest {
        val logBridge = LogBridge(testCoroutineScope)
        val collectionJob = launch {
            logBridge.logs.collect { collector.emit(it) }
        }
        testCoroutineScope.pauseDispatcher()
        (0..99).forEach { _ ->
            logBridge.emit()
        }
        verifyNoMoreInteractions(collector)
        testCoroutineScope.resumeDispatcher()
        verify(collector, times(100)).emit(any())
        collectionJob.cancel()
    }

    @Test
    fun `check logBridge overflows as expected`() : Unit = testCoroutineScope.runBlockingTest {
        val logBridge = LogBridge(testCoroutineScope, 10, 0, BufferOverflow.DROP_OLDEST)
        testCoroutineScope.pauseDispatcher()
        (0..99).forEach { it ->
            logBridge.emit()
        }
        val collectionJob = launch {
            logBridge.logs.collect { collector.emit(it) }
        }
        verifyNoMoreInteractions(collector)
        testCoroutineScope.resumeDispatcher()
        //We only receive values from 90..99
        verify(collector, times(10)).emit(argThat {
            val logState = this.message as Data.Done<Int>
            logState.result >= 90
        })
        verifyNoMoreInteractions(collector)
        collectionJob.cancel()
    }
}