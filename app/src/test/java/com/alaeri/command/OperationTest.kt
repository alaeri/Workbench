//package com.alaeri.operation
//
//import com.alaeri.operation.id.DefaultIdStore
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.FlowCollector
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.runBlocking
//import opOld
//import org.junit.After
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Test
//
///**
// * Created by Emmanuel Requier on 27/04/2020.
// */
//fun Any.key(): String = DefaultIdStore.instance.keyOf(this).toString()
//class OperationTest {
//
//    @Before
//    fun prepare(){
//        DefaultIdStore.create()
//    }
//
//    @After
//    fun clean(){
//        DefaultIdStore.reset()
//    }
//
//    @Test
//    fun testBasicOperation() = runBlocking {
//        val testCollector: FlowCollector<CommandState<String>> = object : FlowCollector<CommandState<String>>{
//            override suspend fun emit(value: CommandState<String>) {
//                println("collected: $value")
//            }
//        }
//        //val stringOperation = Operation<String>("blam")
//        val catString = opOld(testCollector) {
//            "Cat"
//        }
//        assertEquals("Cat", catString)
//    }
//
//    @Test
//    fun testBasicOp() = runBlocking {
//        val testCollector: FlowCollector<CommandState<String>> = object : FlowCollector<CommandState<String>>{
//            override suspend fun emit(value: CommandState<String>) {
//                println("collected: $value")
//            }
//        }
//        val catString = opOld(testCollector){
//            val canSeeCats : Boolean = op("checkCanViewCats") {
//                false
//            }
//            if(canSeeCats){
//                "Cat"
//            }else{
//                "Dog"
//            }
//        }
//        assertEquals("Dog", catString)
//
//    }
//
//    @Test
//    fun testFoldWithSimpleFlow() = runBlocking{
//        val testCollector: FlowCollector<CommandState<String>> = object : FlowCollector<CommandState<String>>{
//            override suspend fun emit(value: CommandState<String>) {
//                println("collected: $value")
//            }
//        }
//        val catString = opOld(testCollector){
//            val count : Long = op("aCat"){
//                val b : Boolean = fold("canViewCat"){
//                    nestedOperationWithoutLink()
//                }
//                if(b){
//                    2L
//                }else{
//                    10L
//                }
//            }
//            "$count Cats"
//        }
//        assertEquals("10 Cats", catString)
//    }
//
//    private fun nestedOperationWithoutLink(): Flow<CommandState<Boolean>> {
//        return flow {
//            emit(CommandState.Starting())
//            (0..100).forEach {
//                delay(1)
//                emit(CommandState.Progress(it, 100))
//            }
//            emit(CommandState.Waiting())
//            delay(1)
//            emit(CommandState.Done(false))
//        }
//    }
//
//    @Test
//    fun testFoldWithOperationContext() = runBlocking {
//        val testCollector: FlowCollector<CommandState<String>> = object : FlowCollector<CommandState<String>>{
//            override suspend fun emit(value: CommandState<String>) {
//                println("collected: $value")
//            }
//        }
//        val catString = opOld(testCollector){
//            val count : Long = op{
//                val b : Boolean = fold("canViewCat"){
//                    nestedOperationWithOperationContex(this@fold)
//                }
//                if(b){
//                    2L
//                }else{
//                    10L
//                }
//            }
//            "$count Cats"
//        }
//        assertEquals("10 Cats", catString)
//    }
//
//    private fun <PT> nestedOperationWithOperationContex(operationContext: OperationContext<PT,Boolean>): Flow<CommandState<Boolean>> {
//        return operationContext.spread {
//            (0..100).forEach {
//                delay(1)
//                collector.emit(CommandState.Progress(it, 100))
//            }
//            collector.emit(CommandState.Waiting())
//            delay(1)
//            false
//        }
//    }
//
//    @Test
//    fun testOperationContext() = runBlocking{
//        val testCollector: FlowCollector<CommandState<String>> = object : FlowCollector<CommandState<String>>{
//            override suspend fun emit(value: CommandState<String>) {
//                println("collected: $value")
//            }
//        }
//        val result = opOld( testCollector) {
//            val childResult: Int = op {
//                1
//            }
//            val childResult2: Any = op("child2") {
//                Any()
//            }
//            "result"
//        }
//        assertEquals("result", result)
//    }
//}