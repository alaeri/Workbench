package com.alaeri.log.core.collector

import com.alaeri.log.core.Data
import com.alaeri.log.core.Tag
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by Emmanuel Requier on 16/12/2020.
 */
class CollectorSetTest {
    val collectorsSet = LogCollectorsSet(setOf())
    val other: LogCollector = mock {  }

    @Test
    fun `test summation has expected behaviour with other collector`(){
        val result = collectorsSet + other
        assertEquals(LogCollectorsSet::class.java, result.javaClass)
        val resultSet = result as LogCollectorsSet
        assertEquals(1, resultSet.collectors.size)
        assertTrue(other == resultSet.collectors.first())
    }

    @Test
    fun `test summation has expected behaviour with null collector`(){
        val nullOther: LogCollector? = null
        val result = collectorsSet + nullOther
        assertEquals(LogCollectorsSet::class.java, result.javaClass)
        val resultSet = result as LogCollectorsSet
        assertEquals(0, resultSet.collectors.size)
    }

    @Test
    fun `test summation has expected behaviour with other list collector`(){
        val result = collectorsSet + LogCollectorsSet(setOf(other))
        assertEquals(LogCollectorsSet::class.java, result.javaClass)
        val resultSet = result as LogCollectorsSet
        assertEquals(1, resultSet.collectors.size)
        assertTrue(other == resultSet.collectors.first())
    }

    @Test
    fun `test emit calls emit on set members`(){
        val tag: Tag = mock {  }
        val data: Data = mock {  }
        val result = collectorsSet + LogCollectorsSet(setOf(other))
        assertEquals(LogCollectorsSet::class.java, result.javaClass)
        val resultSet = result as LogCollectorsSet
        result.emit()
        verify(other).emit()
    }

}