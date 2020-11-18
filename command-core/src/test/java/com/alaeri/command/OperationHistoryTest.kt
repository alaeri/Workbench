package com.alaeri.command

import com.alaeri.command.core.IInvokationContext
import com.alaeri.command.core.invoke
import com.alaeri.command.entity.Catalog
import com.alaeri.command.history.id.DefaultIdStore
import com.alaeri.command.history.spread
import kotlinx.coroutines.*
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

    lateinit var  testCoroutineScope: TestCoroutineScope
    lateinit var logger: IInvokationContext<Int, Int>.(CommandState<Int>) -> Unit
    val list = mutableListOf<CommandState<*>>()

    val commandRoot = buildCommandRoot(this, "flatten", CommandNomenclature.Test, object : DefaultIRootCommandLogger{
        override fun log(context: IInvokationContext<*, *>, state: CommandState<*>) {
           list.add(state)
        }

    })
    val owner = object : ICommandRootOwner {
        override val commandRoot: AnyCommandRoot
            get() = this@OperationHistoryTest.commandRoot

    }

    @Before
    fun prepare(){
        testCoroutineScope = TestCoroutineScope()
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
        val count = owner.invokeSuspendingRootCommand<Int>("test", CommandNomenclature.Test){
            invoke{
                catalog.count()
            }
            1
        }
        assertEquals(1, count)
        assertEquals(4, list.size)
        val flatList = list.flatMap { spread(commandRoot, it, 0, commandRoot) }
        assertTrue(flatList.none { it.state is CommandState.SubCommand<*, *> })
        assertEquals(4, flatList.size)
        flatList.forEach { println(it) }
        val firstElement = flatList[0]
        val secondElement = flatList[1]
        val thirdElement = flatList[2]
        assertEquals(commandRoot.invoker.owner, firstElement.parentContext.invoker.owner)
        assertEquals(commandRoot.invoker.owner, firstElement.operationContext.command.owner)
        assertEquals(catalog, secondElement.operationContext.command.owner)
        assertEquals(owner, secondElement.operationContext.invoker.owner)
    }
    object shouldBeEmittedLast
    @ExperimentalCoroutinesApi
    @Test
    fun testSerialize() = runBlocking{
        //FIXME
//        withContext(coroutineContext + CoroutineExceptionHandler { coroutineContext, throwable ->
//            println("error")
//        }){
//            supervisorScope {
//                try{
//                    val catalog = Catalog()
//                    val count = owner.invokeSuspendingRootCommand<Int>("test", CommandNomenclature.Test){
//
//                        suspendInvokeAndFold{
//                            catalog.downloadAll()
//                        }
//                        println("csA: $coroutineContext + scope: $this")
//                        1
//
//                    }
////                println("csG: ${this}")
//////                assertEquals(1, count)
//////                assertEquals(5, list.size)
//////                delay(300)
//////                val flatList = list.flatMap { spread(commandRoot, it, 0, commandRoot) }
//////                flatList.map { serialize(it.parentContext, it.operationContext, it.state, it.depth) { DefaultIdStore.instance.keyOf(it) } }.forEach { println(it) }
//////                delay(300)
//////                testCoroutineScope.advanceUntilIdle()
//                }catch (e: IllegalStateException){
//                    println("error of job already completed")
//                }
//            }
//
//        }
    }

//    @Test
//    fun testFocus() = testCoroutineScope.runBlockingTest{
//        val catalog = Catalog()
//        var command: ICommand<*>? = null
//        val count = owner.invokeRootCommand<Int>("test", CommandNomenclature.Test){
//            invoke{
//                catalog.count().apply { command = this }
//            }
//            1
//        }
//        val flatList = list.flatMap { spread(iOperationContext, it, 0, iOperationContext) }
//        val serializedList = flatList.map { serialize(it.parentContext, it.operationContext,it.state, it.depth) { DefaultIdStore.instance.keyOf(it) } }.onEach { println(it) }
//        val focusedCatalog = serializedList.flatMap { it.withFocus(catalog) }.forEach { println(it) }
//        val focusedCommand = serializedList.flatMap { it.withFocus(command!!) }.forEach { println(it) }
//        val focusedCoroutine = serializedList.flatMap { it.withFocus(this) }.forEach { println(it) }
//
//    }

}