package com.alaeri.log.extra.serializer

import com.alaeri.log.core.LogState
import com.alaeri.log.core.context.LogContext
import com.alaeri.log.serialize.serialize.LogDataAndState
import com.alaeri.log.serialize.serialize.LogRepresentation
import com.alaeri.log.serialize.serialize.LogSerializer
import com.alaeri.log.serialize.serialize.SerializedLogState
import com.alaeri.log.serialize.serialize.mapping.EntityTransformer
import com.alaeri.log.serialize.serialize.mapping.LogTypedTransformer
import com.alaeri.log.serialize.serialize.representation.EntityRepresentation
import com.nhaarman.mockitokotlin2.*
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.Test
import java.lang.RuntimeException

/**
 * Created by Emmanuel Requier on 19/12/2020.
 */
class LogSerializerTest {

    val logTypedTransformer: LogTypedTransformer<LogContext, LogRepresentation<LogContext>> = mock {}
    val entityTransformer: EntityTransformer<Any, EntityRepresentation<Any>> = mock {}

    val logSerializer = LogSerializer(logTypedTransformer, entityTransformer)

    @Test
    fun `test serialization works as expected when for starting`(){
        val mockedParams : List<Any> = listOf("Param1", "Param2")
        val logState: LogState.Starting = mock {
            on { params } doReturn mockedParams
        }
        val logContext : LogContext = mock {  }
        val logDataAndState : LogDataAndState = LogDataAndState(logContext, logState)
        val serialized = logSerializer.serialize(logDataAndState)
        assertNotNull(serialized)
        verify(logTypedTransformer).transformOrNull(logContext)
        verify(entityTransformer).transform(eq("Param1"))
        verify(entityTransformer).transform(eq("Param2"))
        verifyNoMoreInteractions(entityTransformer)
        verifyNoMoreInteractions(logTypedTransformer)
        assertEquals(SerializedLogState.Start::class.java, serialized.state.javaClass)
    }

    @Test
    fun `test serialization works as expected when for done`(){
        val logState: LogState.Done<Any> = mock {
            on { result } doReturn "Result"
        }
        val logContext : LogContext = mock {  }
        val logDataAndState : LogDataAndState = LogDataAndState(logContext, logState)
        val serialized = logSerializer.serialize(logDataAndState)
        assertNotNull(serialized)
        verify(logTypedTransformer).transformOrNull(logContext)
        verify(entityTransformer).transform(eq("Result"))
        verifyNoMoreInteractions(entityTransformer)
        verifyNoMoreInteractions(logTypedTransformer)
        assertEquals(SerializedLogState.Success::class.java, serialized.state.javaClass)

    }

    @Test
    fun `test serialization works as expected when for error`(){
        val logState: LogState.Failed = mock {
            on { exception } doReturn RuntimeException("Unfair")
        }
        val logContext : LogContext = mock {  }
        val logDataAndState : LogDataAndState = LogDataAndState(logContext, logState)
        val serialized = logSerializer.serialize(logDataAndState)
        assertNotNull(serialized)
        verify(logTypedTransformer).transformOrNull(logContext)
        verify(entityTransformer).transform(argThat { this is RuntimeException && this.message == "Unfair" })
        verifyNoMoreInteractions(entityTransformer)
        verifyNoMoreInteractions(logTypedTransformer)
        assertEquals(SerializedLogState.Error::class.java, serialized.state.javaClass)
    }
}