package com.alaeri.log.core.basic

import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.Log.Tag
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Created by Emmanuel Requier on 16/12/2020.
 */
class BasicLogEnvironmentTest {

    private val tag : Tag = mock{}
    private val logCollector : LogCollector = mock{}
    private val basicLogEnvironment = BasicLogEnvironment(tag, logCollector)

    //private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    @Before
    fun setUp() {
        //Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        //Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        //mainThreadSurrogate.close()
    }

    @Test
    fun testBlockingUnit(){
       val unit: Unit = basicLogEnvironment.logBlocking(arrayOf("param1")){
           verify(logCollector).emit(any())
       }
       assertEquals(Unit, unit)
       verify(logCollector, times(2)).emit(any())
    }

    @Test
    fun testBlockingException(){
        val exception = RuntimeException("error")
        val result = kotlin.runCatching {
            basicLogEnvironment.logBlocking(arrayOf("param1")){
                throw exception
            }
        }
        verify(logCollector, times(2)).emit(any())
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun testBlockingValue(){
        val result = kotlin.runCatching {
            basicLogEnvironment.logBlocking(arrayOf("param1")){
                "result"
            }
        }
        verify(logCollector, times(2)).emit(any())
        assertEquals("result", result.getOrNull())
    }

    @Test
    fun testSuspendingUnit() = testCoroutineScope.runBlockingTest{
        val unit: Unit = basicLogEnvironment.logSuspending("param1"){
            verify(logCollector).emit(any())
        }
        assertEquals(Unit, unit)
        verify(logCollector, times(2)).emit(any())
    }

    @Test
    fun testSuspendingException() = testCoroutineScope.runBlockingTest{
        val exception = RuntimeException("error")
        val result = kotlin.runCatching {
            basicLogEnvironment.logSuspending<Unit>("param1"){
                throw exception
            }
        }
        verify(logCollector, times(2)).emit(any())
        verifyNoMoreInteractions(logCollector)
        //Here we need to compare message as exceptions do not match assertEquals?
        //TODO investigate
        assertEquals(exception.message, result.exceptionOrNull()?.message)
    }

    @Test
    fun testSuspendingValue() = testCoroutineScope.runBlockingTest {
        val result = kotlin.runCatching {
            basicLogEnvironment.logSuspending("param1"){
                "result"
            }
        }
        verify(logCollector, times(2)).emit(any())
        verifyNoMoreInteractions(logCollector)
        assertEquals("result", result.getOrNull())
    }



}