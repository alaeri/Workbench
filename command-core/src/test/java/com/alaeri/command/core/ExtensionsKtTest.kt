package com.alaeri.command.core

import com.alaeri.command.*
import com.alaeri.command.core.root.*
import com.alaeri.command.core.suspend.suspendingCommand
import com.alaeri.command.mock.entity.Catalog
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by Emmanuel Requier on 02/05/2020.
 */
class ExtensionsKtTest {

    private lateinit var owner: ICommandScopeOwner
    private val testCoroutineScope = TestCoroutineScope()
    lateinit var logger: DefaultRootCommandScope

    val list = mutableListOf<CommandState<*>>()

    @Before
    fun prepare(){
        logger = buildRootCommandScope(
            this,
            null,
            CommandNomenclature.Test,
            object : ICommandLogger {
                override fun log(context: IParentCommandScope<*, *>, state: CommandState<*>) {
                    list.add(state)
                    println(state)
                }
            })
        owner = object : ICommandScopeOwner {
            override val commandScope: DefaultRootCommandScope
                get() = logger

        }
//        logger = object : IInvokationContext<Int, Int> {
//            override val command = object : ICommand<Int> {
//                override val owner: Any = this
//                override val nomenclature: CommandNomenclature = CommandNomenclature.Test
//                override val name: String? = "test"
//            }
//            override val invoker: Invoker<Int> = object : Invoker<Int> {
//                override val owner: Any = this
//            }
//
//            override fun emit(opState: CommandState<Int>) {
//
//            }
//        }
    }

    @After
    fun clean(){
        testCoroutineScope.cleanupTestCoroutines()
        list.clear()
    }

    @Test
    fun testBasicSuspendOperationWorks() = runBlocking {
        val value = owner.invokeSuspendingRootCommand<Int>("test",
            CommandNomenclature.Test
        ){
            1
        }
        assertEquals(1, value)
    }

    @Test
    fun testBasicSyncOperationWorks() = runBlocking {
        val value2 = owner.invokeRootCommand<Int>("test",
            CommandNomenclature.Test
        ){
            2
        }
        assertEquals(2, value2)
    }



    @Test
    fun testMoreComplexSuspendOperation(){
        runBlocking {
            val value = owner.invokeSuspendingRootCommand<Int>("test",
                CommandNomenclature.Test
            ){
                val count = suspendInvokeAndFold {
                    suspendingCommand<Int> {
                        val a: Int =  1
                        a
                    }
                }
                emit(
                    CommandState.Update(
                        Progress(
                            1,
                            2
                        )
                    )
                )
                emit(Step("calling OtherCount"))
                val otherCount = invoke { command<Int> { 0 } }
                count + otherCount
            }
            assertEquals(1, value)
        }
        val value2 = owner.invokeRootCommand<Int>("test",
            CommandNomenclature.Test
        ){
            val count = invoke { command<Int>{  2  } }
            testCoroutineScope.launch {
                val count = suspendInvokeAndFold {
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
        val count = owner.invokeSuspendingRootCommand<Int>("test",
            CommandNomenclature.Test
        ){
            invoke {
                catalog.count()
            }
        }
        assertEquals(1, count)
    }

    @Test
    fun testContainsAllEvents() = testCoroutineScope.runBlockingTest {
        val catalog = Catalog()
        val count = owner.invokeSuspendingRootCommand<Int>("test",
            CommandNomenclature.Test
        ){
            invoke {
                catalog.count()
            }
            1
        }
        assertEquals(1, count)
        assertEquals(4, list.size)
        val thirdEvent = list[2]
        assertTrue(thirdEvent is CommandState.SubCommand<*, *>)
        val operationStateSubOp = thirdEvent as CommandState.SubCommand<*, *>
        val (subOpC, _) = operationStateSubOp.subCommandAndState
        assertEquals(catalog, subOpC.command.owner)
        assertEquals(owner, subOpC.invoker.owner)
    }


}