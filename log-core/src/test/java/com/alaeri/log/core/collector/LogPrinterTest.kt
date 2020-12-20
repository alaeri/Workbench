package com.alaeri.log.core.collector

import org.junit.Test

/**
 * Created by Emmanuel Requier on 17/12/2020.
 */
class LogPrinterTest {

    @Test
    fun testPrint(){
        val logPrinter = LogPrinter()
        logPrinter.emit()
    }

}

