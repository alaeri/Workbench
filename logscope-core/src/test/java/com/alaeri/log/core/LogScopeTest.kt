package com.alaeri.log.core

import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.collector.LogPrinter
import com.alaeri.log.core.context.EmptyLogContext
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Created by Emmanuel Requier on 12/12/2020.
 */
class LogScopeTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private val logCollector: LogCollector = mock {  }

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
    fun testSomeUI() : Unit = runBlocking {
        launch(Dispatchers.Main) {  // Will be launched in the mainThreadSurrogate dispatcher
            val greeting = log(collector = logCollector) {
                "SALUT"
            }
            verify(logCollector).emit(argThat { this is EmptyLogContext }, eq(LogState.Starting(listOf())))
            verify(logCollector).emit(argThat { this is EmptyLogContext }, eq(LogState.Done<String>("SALUT")))
            verifyNoMoreInteractions(logCollector)
        }
    }

    @Test
    fun testHierarchy() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            log(collector = LogPrinter()) {
                // Will be launched in the mainThreadSurrogate dispatcher
                val result = TestParent().piou()
                assertEquals("pi", result)
            }
        }
    }

    class TestParent(){
        suspend fun piou() = log {
            TestChild().piii()
        }
    }
    class TestChild(){
        suspend fun piii() = log {
            "pi"
        }
    }

    class BlockingParent(){
        val blockingChild = BlockingChild()
        fun piou()  = logBlocking {
            blockingChild.pi()
        }
    }

    class BlockingChild(){
        fun pi() = logBlocking {
            "pi"
        }
    }

    @Test
    fun `check that blocking function works`(){
        val blockingParent = BlockingParent()
        logBlocking(collector = LogPrinter()) {
            // Will be launched in the mainThreadSurrogate dispatcher
            val result = blockingParent.piou()
            assertEquals("pi", result)
        }
    }

}