package com.alaeri.command.core.root

import com.alaeri.command.*
import com.alaeri.command.core.CommandScope
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ICommandScopeOwnerKtTest {

    private lateinit var commandScopeOwner: ICommandScopeOwner
    private lateinit var _commandRoot: DefaultRootCommandScope
    private lateinit var _body: CommandScope<Unit>.()->Unit
    private lateinit var _suspendingbody: suspend CommandScope<Unit>.()->Unit

    @Before
    fun prepare(){
        _commandRoot = mock {

        }
        commandScopeOwner = mock<ICommandScopeOwner>{
            onBlocking { commandScope } doReturn (_commandRoot)
        }
        _body = mock {

        }
        _suspendingbody = mock {  }
    }

    @After
    fun clean(){

    }

    @Test
    fun `test that root command sync invokation works as expected`(){
        commandScopeOwner.invokeRootCommand<Unit>(
            name = "test",
            commandNomenclature = CommandNomenclature.Test, _body)
        inOrder(_commandRoot, _body){
            verify(_commandRoot).emit(argThat { this is Starting<*> })
            verify(_body).invoke(any())
        }
        verify(_commandRoot).emit(argThat { this is CommandState.Done<*> })
        verifyNoMoreInteractions(_commandRoot)
        verifyNoMoreInteractions(_body)
    }

    @Test(expected = RuntimeException::class)
    fun `test that a throwable will be propagated during sync command invokation`(){
        _body = mock { onBlocking { invoke(any()) }doThrow(RuntimeException("Error")) }
        try {
            commandScopeOwner.invokeRootCommand<Unit>(
                name = "test",
                commandNomenclature = CommandNomenclature.Test, _body)
        }finally {
            inOrder(_commandRoot, _body){
                verify(_commandRoot).emit(argThat { this is Starting<*> })
                verify(_body).invoke(any())
            }
            verify(_commandRoot).emit(argThat { this is CommandState.Failure<*> })
            verifyNoMoreInteractions(_commandRoot)
            verifyNoMoreInteractions(_body)
        }
    }

    @Test
    fun `test that suspending root command invokation works as expected`()= runBlockingTest{
        _suspendingbody = { println("invoked: $this") } //Mocking the suspending body fails with wrong number of matchers
        commandScopeOwner.invokeSuspendingRootCommand<Unit>(
            name = "test",
            commandNomenclature = CommandNomenclature.Test, _suspendingbody)
        verify(_commandRoot).emit(argThat { this is Starting<*> })
        verify(_commandRoot).emit(argThat { this is CommandState.Done<*> })
        verifyNoMoreInteractions(_commandRoot)
    }

    @Test(expected = RuntimeException::class)
    fun `test that a throwable will be propagated during suspending command invokation`() = runBlockingTest{
        _suspendingbody = mock { onBlocking { invoke(any()) }doThrow(RuntimeException("Error")) }
        try {
            commandScopeOwner.invokeSuspendingRootCommand<Unit>(
                name = "test",
                commandNomenclature = CommandNomenclature.Test, _suspendingbody)
        }finally {
            inOrder(_commandRoot, _suspendingbody){
                verify(_commandRoot).emit(argThat { this is Starting<*> })
                verify(_suspendingbody).invoke(any())
            }
            verify(_commandRoot).emit(argThat { this is CommandState.Failure<*> })
            verifyNoMoreInteractions(_commandRoot)
            verifyNoMoreInteractions(_body)
        }
    }
}