package com.alaeri.log.core

import com.alaeri.log.core.context.ListTag
import com.alaeri.log.core.Tag
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
        val list = ListTag(listOf())
        val other : Tag = mock {  }
        val result = list + other
        assertEquals(ListTag::class.java, result.javaClass)
        val resultList = result as ListTag
        assertEquals(1, resultList.list.size)
        assertEquals(other, resultList.list.first())
    }

    @Test
    fun testNullLogContextAddedToListIsIgnored(){
        val list = ListTag(listOf())
        val other : Tag? = null
        val result = list + other
        assertEquals(ListTag::class.java, result.javaClass)
        val resultList = result as ListTag
        assertTrue(resultList === list)
        assertEquals(0, resultList.list.size)
    }

}