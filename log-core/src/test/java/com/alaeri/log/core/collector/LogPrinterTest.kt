package com.alaeri.log.core.collector

import com.alaeri.log.core.Log
import com.alaeri.log.core.context.EmptyTag
import com.nhaarman.mockitokotlin2.any
import org.junit.Test

/**
 * Created by Emmanuel Requier on 17/12/2020.
 */
class LogPrinterTest {

    @Test
    fun testPrint(){
        val logPrinter = LogPrinter()
        logPrinter.emit(Log(EmptyTag(), Log.Message.Starting(emptyList())))
    }

}

