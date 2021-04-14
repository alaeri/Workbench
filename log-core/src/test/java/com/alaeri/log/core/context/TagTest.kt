package com.alaeri.log.core.context

import com.alaeri.log.core.Log.Tag
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by Emmanuel Requier on 16/12/2020.
 */
class TagTest {

    @Test
    fun testSummationWithOtherLogContext(){
        val tag : Tag = spy(EmptyTag())
        val other: Tag = mock {  }
        val result = tag + other
        assertEquals(ListTag::class.java, result.javaClass)
    }

    @Test
    fun testSummationWithNullLogContext(){
        val tag : Tag = spy(EmptyTag())
        val other: Tag? = null
        val result = tag + other
        assertTrue(tag === result)
    }

    @Test
    fun testSummationWithListLogContext(){
        val tag : Tag = spy(EmptyTag())
        val mock: Tag = mock {  }
        val other: ListTag = mock { on { list } doReturn listOf(mock) }
        val result = tag + other
        assertEquals(ListTag::class.java, result.javaClass)
        val listResult = result as ListTag
        assertEquals(2, listResult.list.size)
        assertEquals(mock, listResult.list[1])
        assertEquals(tag, listResult.list[0])

    }

}