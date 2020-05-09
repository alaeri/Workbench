package com.alaeri.command

import com.alaeri.command.entity.Catalog
import com.alaeri.command.core.IInvokationContext
import com.alaeri.command.core.suspendInvokeAndFold
import com.alaeri.command.core.command
import com.alaeri.command.core.invoke
import com.alaeri.command.core.suspend.suspendingCommand
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by Emmanuel Requier on 02/05/2020.
 */
class Operation2Test {

    private val testCoroutineScope = TestCoroutineScope()
    lateinit var logger: IInvokationContext<*, *>.(CommandState<Int>) -> Unit
    val list = mutableListOf<CommandState<Int>>()

    @Before
    fun prepare(){
        logger = { t ->
            list.add(t)
            println(t)
        }
    }

    @After
    fun clean(){
        testCoroutineScope.cleanupTestCoroutines()
        list.clear()
    }

    @Test
    fun testBasicSuspendOperationWorks() = runBlocking {
        val value = invokeSuspendingCommand<Int>({ t -> println(t)}){
            1
        }
        assertEquals(1, value)
    }

    @Test
    fun testBasicSyncOperationWorks() = runBlocking {
        val value2 = invokeSyncCommand<Int>({ t-> println(t)}){
            2
        }
        assertEquals(2, value2)
    }



    @Test
    fun testMoreComplexSuspendOperation(){
        runBlocking {
            val value = invokeSuspendingCommand(logger){
                val count = suspendInvokeAndFold {
                    suspendingCommand<Int> {
                        val a: Int =  1
                        a
                    }
                }
                emit(CommandState.Update(Progress(1, 2)))
                emit(Step("calling OtherCount"))
                val otherCount = invoke { command<Int> { 0 } }
                count + otherCount
            }
            assertEquals(1, value)
        }
        val value2 = invokeSyncCommand<Int>({ t -> println(t)}){
            val count = invoke { command<Int>{  2  } }
            testCoroutineScope.launch {
                suspendInvokeAndFold {
                    suspendingCommand<Int> {
                        delay(100)
                        0
                    }
                }
            }
            count
        }
        assertEquals(2, value2)
        testCoroutineScope.advanceUntilIdle()

    }

    @Test
    fun testWorksWithClass() = testCoroutineScope.runBlockingTest {
        val catalog = Catalog()
        val count = invokeSuspendingCommand(logger){
            invoke {
                catalog.count()
            }
        }
        assertEquals(1, count)
    }

    @Test
    fun testContainsAllEvents() = testCoroutineScope.runBlockingTest {
        val catalog = Catalog()
        val count = invokeSuspendingCommand(logger){
            invoke {
                catalog.count()
            }
            1
        }
        assertEquals(1, count)
        assertEquals(4, list.size)
        val thirdEvent = list[2]
        assertTrue(thirdEvent is CommandState.SubCommand<*,*>)
        val operationStateSubOp = thirdEvent as CommandState.SubCommand<*, *>
        val (subOpC, _) = operationStateSubOp.subCommandAndState
        assertEquals(catalog, subOpC.command.owner)
        assertEquals(this, subOpC.invoker.owner)
    }


}