package com.alaeri.log.core

import com.alaeri.log.core.child.ChildLogEnvironmentFactory
import com.alaeri.log.core.child.CoroutineLogEnvironment
import com.alaeri.log.core.collector.LogCollector
import com.alaeri.log.core.collector.LogPrinter
import com.alaeri.log.core.context.EmptyTag
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.CoroutineContext

/**
 * Created by Emmanuel Requier on 12/12/2020.
 */

suspend inline fun <reified T> logBuggy(tag: Log.Tag = EmptyTag(),
                                   collector: LogCollector? = null,
                                   vararg params: Any? = arrayOf(),
                                   crossinline body :suspend ()->T) : T  =
    LogConfig.inlinedSuspendLog(tag, collector, *params){
        body.invoke()
    }

suspend inline fun <reified T> log(tag: Log.Tag = EmptyTag(),
                                   collector: LogCollector? = null,
                                   vararg params: Any? = arrayOf(),
                                   crossinline body :suspend ()->T) : T  =
    LogConfig.log(tag, collector, *params){
        body.invoke()
    }

inline fun <reified T> logBlocking(tag: Log.Tag = EmptyTag(),
                                   collector: LogCollector? = null,
                                   vararg params: Any? = arrayOf(),
                                   body :()->T): T =
    LogConfig.logBlocking(tag, collector, *params) {
        body.invoke()
    }

class IntegrationTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private val logCollector: LogCollector = mock {  }

    @Before
    fun setUp() {
        LogConfig.logEnvironmentFactory = ChildLogEnvironmentFactory
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun testSomeUI() : Unit = runBlocking {
        val salut = "salut"
        launch(Dispatchers.Main) {  // Will be launched in the mainThreadSurrogate dispatcher
            val greeting = log(collector = logCollector,  params = *arrayOf("bump")) {
                salut
            }
            verify(logCollector, times(2)).emit(any())
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
        fun piou() = logBlocking {
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

    @Test
    fun `check that suspending function works`()= runBlockingTest{
        val testParent = TestParent()
        log(collector = LogPrinter()) {
            // Will be launched in the mainThreadSurrogate dispatcher
            val result = testParent.piou()
            assertEquals("pi", result)
        }
    }

    val flowParent = flow<String>{
        val coroutineContext = currentCoroutineContext()
        println("currentCoroutineContext flow: ${currentCoroutineContext()}")
        (0..100).forEach {
            delay(100)
            log {
                withContext(coroutineContext){
                    emit("pi: $it")
                }
            }
        }

    }

    val childFlow : Flow<String> = flowParent.log().map {
        println("currentCoroutineContext map: ${currentCoroutineContext()}")
        val result = log{
            "piou $it"
        }
        result
    }

    @Test
    fun `check that flows work`()= runBlockingTest{
        println("currentCoroutineContext start: ${currentCoroutineContext()}")
        log(collector = LogPrinter()) {
            println("currentCoroutineContext first log: ${currentCoroutineContext()}")
            // Will be launched in the mainThreadSurrogate dispatcher
            val result = childFlow.log().first()
            assertEquals("piou pi: 0", result)
        }
    }

    fun <T> Flow<T>.log(): Flow<T>{
        val originalFlow = this
        return flow<T> {
            val childLogEnvironment = ChildLogEnvironmentFactory.suspendingLogEnvironment(EmptyTag(), null)
            val childCoroutineContext = CoroutineLogEnvironment(childLogEnvironment)
            originalFlow
                .onStart { log(EmptyTag(),null,"onStart"){} }
                .onEach { log(EmptyTag(),null,"onEach", it){} }
                .onCompletion { log(EmptyTag(),null,"onCompletion"){} }
                .flowOn(childCoroutineContext).collect {
                    emit(it)
                }
        }
    }

    @Test
    fun `check that suspending inline function is bugged`()= runBlockingTest{
        val testParent = TestParent()
        logBuggy(collector = LogPrinter()) {
            // Will be launched in the mainThreadSurrogate dispatcher
            val result = testParent.piou()
            assertEquals("pi", result)
        }
    }

}