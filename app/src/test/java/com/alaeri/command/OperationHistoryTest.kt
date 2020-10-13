package com.alaeri.command

import com.alaeri.command.android.CommandNomenclature
import com.alaeri.command.core.*
import com.alaeri.command.entity.Catalog
import com.alaeri.command.history.*
import com.alaeri.command.history.id.DefaultIdStore
import defaultKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Created by Emmanuel Requier on 03/05/2020.
 */
@ExperimentalCoroutinesApi
class OperationHistoryTest {

    private val testCoroutineScope = TestCoroutineScope()
    lateinit var logger: IInvokationContext<Int, Int>.(CommandState<Int>) -> Unit
    val list = mutableListOf<CommandState<Int>>()

    @Before
    fun prepare(){
        logger = { t ->
            list.add(t)
            println(t)
        }
        DefaultIdStore.create()
    }

    @After
    fun clean(){
        testCoroutineScope.cleanupTestCoroutines()
        list.clear()
        DefaultIdStore.reset()
    }

    @Test
    fun testFlatten() = testCoroutineScope.runBlockingTest{
        val catalog = Catalog()
        val iOperationContext =
            buildCommandContextA<Int>(this, "flatten", CommandNomenclature.Test, logger)
        val count = invokeSuspendingCommand(iOperationContext){
            invoke{
                catalog.count()
            }
            1
        }
        assertEquals(1, count)
        assertEquals(4, list.size)
        val flatList = list.flatMap { spread(iOperationContext, it) }
        assertTrue(flatList.none { it.state is CommandState.SubCommand<*,*> })
        assertEquals(4, flatList.size)
        flatList.forEach { println(it) }
        val firstElement = flatList[0]
        val secondElement = flatList[1]
        val thirdElement = flatList[2]
        assertEquals(this, firstElement.operationContext.invoker.owner)
        assertEquals(this, firstElement.operationContext.command.owner)
        assertEquals(catalog, secondElement.operationContext.command.owner)
        assertEquals(this, secondElement.operationContext.invoker.owner)
    }
    object shouldBeEmittedLast
    @ExperimentalCoroutinesApi
    @Test
    fun testSerialize() = testCoroutineScope.runBlockingTest{
        val catalog = Catalog()
        val iOperationContext =
            buildCommandContextA<Int>(this, "name", CommandNomenclature.Test, logger)
        val count = invokeSuspendingCommand(iOperationContext){

            coroutineScope {
                suspendInvokeAndFold{
                    catalog.downloadAll()
                }
                println("csA: $coroutineContext")
                Unit
            }

            1

        }
        println("csG: ${this}")
        assertEquals(1, count)
        assertEquals(5, list.size)
        delay(300)
        val flatList = list.flatMap { spread(iOperationContext, it) }
        flatList.map { serialize(it.operationContext, it.state, it.depth) { this.defaultKey() } }.forEach { println(it) }
        delay(300)
        testCoroutineScope.advanceUntilIdle()
    }

    @Test
    fun testFocus() = testCoroutineScope.runBlockingTest{
        val catalog = Catalog()
        val iOperationContext =
            buildCommandContextA<Int>(this, "name", CommandNomenclature.Test, logger)
        var command: ICommand<*>? = null
        val count = invokeSyncCommand(iOperationContext){
            invoke{
                catalog.count().apply { command = this }
            }
            1
        }
        val flatList = list.flatMap { spread(iOperationContext, it) }
        val serializedList = flatList.map { serialize(it.operationContext, it.state, it.depth) { this.defaultKey() } }.onEach { println(it) }
        val focusedCatalog = serializedList.flatMap { it.withFocus(catalog) }.forEach { println(it) }
        val focusedCommand = serializedList.flatMap { it.withFocus(command!!) }.forEach { println(it) }
        val focusedCoroutine = serializedList.flatMap { it.withFocus(this) }.forEach { println(it) }

    }

}