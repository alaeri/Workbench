package com.alaeri.log.core.collector

import com.alaeri.log.core.LogState
import com.alaeri.log.core.context.EmptyLogContext
import org.junit.Test

/**
 * Created by Emmanuel Requier on 17/12/2020.
 */
class LogPrinterTest {

    @Test
    fun testPrint(){
        val logPrinter = LogPrinter()
        logPrinter.emit(EmptyLogContext(), LogState.Starting(listOf()))
    }

}

