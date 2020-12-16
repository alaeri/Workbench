package com.alaeri.log.core.context

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by Emmanuel Requier on 16/12/2020.
 */
class LogContextTest {

    @Test
    fun testSummationWithOtherLogContext(){
        val logContext : LogContext = spy(EmptyLogContext())
        val other: LogContext = mock {  }
        val result = logContext + other
        assertEquals(ListLogContext::class.java, result.javaClass)
    }

    @Test
    fun testSummationWithNullLogContext(){
        val logContext : LogContext = spy(EmptyLogContext())
        val other: LogContext? = null
        val result = logContext + other
        assertTrue(logContext === result)
    }

    @Test
    fun testSummationWithListLogContext(){
        val logContext : LogContext = spy(EmptyLogContext())
        val mock: LogContext = mock {  }
        val other: ListLogContext = mock { on { list } doReturn listOf(mock) }
        val result = logContext + other
        assertEquals(ListLogContext::class.java, result.javaClass)
        val listResult = result as ListLogContext
        assertEquals(2, listResult.list.size)
        assertEquals(mock, listResult.list[1])
        assertEquals(logContext, listResult.list[0])

    }

}