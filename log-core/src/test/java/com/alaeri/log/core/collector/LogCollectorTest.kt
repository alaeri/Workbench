package com.alaeri.log.core.collector

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import junit.framework.Assert.assertEquals
import org.junit.Test

/**
 * Created by Emmanuel Requier on 17/12/2020.
 */
class LogCollectorTest {

    val logCollector : LogCollector = spy(NoopCollector)

    @Test
    fun `log collectors can be summed`(){
        val rlc = logCollector.plus(mock {  })
        assertEquals(LogCollectorsSet::class.java, rlc.javaClass)
        val rlcs = rlc as LogCollectorsSet
        assertEquals(rlcs.collectors.size, 2)
    }
}