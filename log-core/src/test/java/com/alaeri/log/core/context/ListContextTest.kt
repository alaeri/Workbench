package com.alaeri.log.core

import com.alaeri.log.core.context.ListLogContext
import com.alaeri.log.core.context.LogContext
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by Emmanuel Requier on 16/12/2020.
 */
class ListContextTest{

    @Test
    fun testCanAddLogContextToList(){
        val list = ListLogContext(listOf())
        val other : LogContext = mock {  }
        val result = list + other
        assertEquals(ListLogContext::class.java, result.javaClass)
        val resultList = result as ListLogContext
        assertEquals(1, resultList.list.size)
        assertEquals(other, resultList.list.first())
    }

    @Test
    fun testNullLogContextAddedToListIsIgnored(){
        val list = ListLogContext(listOf())
        val other : LogContext? = null
        val result = list + other
        assertEquals(ListLogContext::class.java, result.javaClass)
        val resultList = result as ListLogContext
        assertTrue(resultList === list)
        assertEquals(0, resultList.list.size)
    }

}