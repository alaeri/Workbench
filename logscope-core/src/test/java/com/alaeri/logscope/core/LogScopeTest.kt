package com.alaeri.logscope.core

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
            val greeting = logScope(LogPrinter() + object : LogMetadata<EmptyLogRepresentation>{}) {
                "SALUT"
            }
        }
    }

    @Test
    fun testHierarchy() : Unit = runBlocking {
        launch(Dispatchers.Main) {
            logScope(LogPrinter() + EmptyLogMetadata) {
                // Will be launched in the mainThreadSurrogate dispatcher
                val result = TestParent().piou()
                assertEquals("pi", result)
            }
        }
    }

    class TestParent(){
        suspend fun piou() = logScope {
            TestChild().piii()
        }
    }
    class TestChild(){
        suspend fun piii() = logScope {
            "pi"
        }
    }

    class BlockingParent(){
        val blockingChild = BlockingChild()
        fun piou()  = blockingLogScope {
            blockingChild.pi()
        }
    }

    class BlockingChild(){
        fun pi() = blockingLogScope {
            "pi"
        }
    }

    @Test
    fun `check that blocking function works`(){
        val blockingParent = BlockingParent()
        blockingLogScope(LogPrinter() + EmptyLogMetadata) {
            // Will be launched in the mainThreadSurrogate dispatcher
            val result = blockingParent.piou()
            assertEquals("pi", result)
        }
    }

}