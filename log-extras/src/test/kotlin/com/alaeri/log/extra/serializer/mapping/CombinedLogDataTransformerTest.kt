package com.alaeri.log.extra.serializer.mapping

import com.alaeri.log.core.child.ChildLogContext
import com.alaeri.log.core.context.ListLogContext
import com.alaeri.log.core.context.LogContext
import com.alaeri.log.extra.serialize.EmptyLogRepresentation
import com.alaeri.log.extra.serialize.LogRepresentation
import com.alaeri.log.extra.serialize.mapping.CombinedLogDataTransformer
import com.alaeri.log.extra.serialize.mapping.LogTypedTransformer
import com.alaeri.log.extra.serialize.representation.FiliationRepresentation
import com.alaeri.log.extra.serialize.representation.ListRepresentation
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by Emmanuel Requier on 18/12/2020.
 */
class CombinedLogDataTransformerTest {

    @Test
    fun `test childLogContext mapping`(){
        val combinedLogDataTransformer = CombinedLogDataTransformer(listOf())
        val emptyMock: LogContext = mock {  }
        val childLogContext : ChildLogContext = mock { on{parentLogContext}doReturn emptyMock }
        val childLogRepresentation = combinedLogDataTransformer.transform(childLogContext)
        assertEquals(FiliationRepresentation::class.java, childLogRepresentation.javaClass)
    }

    @Test
    fun `test list mapping`(){
        val combinedLogDataTransformer = CombinedLogDataTransformer(listOf())
        val emptyMock: LogContext = mock {  }
        val listLogContext : ListLogContext = mock { on{list}doReturn listOf(emptyMock) }
        val listLogRepresentation = combinedLogDataTransformer.transform(listLogContext)
        assertEquals(ListRepresentation::class.java, listLogRepresentation.javaClass)
    }

    @Test
    fun `test inner mapping works`(){
        val emptyLogRepresentation = EmptyLogRepresentation()
        val logTypedMapper : LogTypedTransformer<LogContext, LogRepresentation<LogContext>> = mock {
            onBlocking{transform(any())} doReturn emptyLogRepresentation
            onBlocking{transformOrNull(any())} doReturn emptyLogRepresentation
        }
        val combinedLogDataTransformer = CombinedLogDataTransformer(listOf(logTypedMapper))
        val logContext: LogContext = mock { }
        val mapped = combinedLogDataTransformer.transform(logContext)
        verify(logTypedMapper).transformOrNull(eq(logContext))
        assertEquals(ListRepresentation(listOf(emptyLogRepresentation)), mapped)
    }


}