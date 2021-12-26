package com.alaeri.log.extra.serializer

import com.alaeri.log.core.Log.Message
import com.alaeri.log.core.Log.Tag
import com.alaeri.log.core.Log
import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.serialize.serialize.*
import com.alaeri.log.serialize.serialize.mapping.EntityTransformer
import com.alaeri.log.serialize.serialize.mapping.TagTypedSerializer
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

    val logTypedTransformer: TagTypedSerializer< Tag, SerializedTag> = mock {}
    val entityTransformer: EntityTransformer<Any, EntityRepresentation<Any>> = mock {}
    val identityTransformer: IIdentityTransformer<IdentityRepresentation> = mock{
        onBlocking { transform(any()) }.doReturn(IdentityRepresentation(1, "1"))
    }

    val logSerializer = LogSerializer(logTypedTransformer, entityTransformer, identityTransformer)

    @Test
    fun `test serialization works as expected when for starting`(){
        val mockedParams : List<Any> = listOf("Param1", "Param2")
        val data: Message.Starting = mock {
            on { params } doReturn mockedParams
        }
        val tag : Tag = mock {  }
        val log : Log = Log(tag, data)
        val serialized = logSerializer.serialize(log)
        assertNotNull(serialized)
        verify(logTypedTransformer).transformOrNull(tag)
        verify(entityTransformer).transform(eq("Param1"))
        verify(entityTransformer).transform(eq("Param2"))
        verifyNoMoreInteractions(entityTransformer)
        verifyNoMoreInteractions(logTypedTransformer)
        assertEquals(SerializedLogMessage.Start::class.java, serialized.message.javaClass)
    }

    @Test
    fun `test serialization works as expected when for done`(){
        val data: Message.Done<Any> = mock {
            on { result } doReturn "Result"
        }
        val tag : Tag = mock {  }
        val log : Log = Log(tag, data)
        val serialized = logSerializer.serialize(log)
        assertNotNull(serialized)
        verify(logTypedTransformer).transformOrNull(tag)
        verify(entityTransformer).transform(eq("Result"))
        verifyNoMoreInteractions(entityTransformer)
        verifyNoMoreInteractions(logTypedTransformer)
        assertEquals(SerializedLogMessage.Success::class.java, serialized.message.javaClass)

    }

    @Test
    fun `test serialization works as expected when for error`(){
        val data: Message.Failed = mock {
            on { exception } doReturn RuntimeException("Unfair")
        }
        val tag : Tag = mock {  }
        val log : Log = Log(tag, data)
        val serialized = logSerializer.serialize(log)
        assertNotNull(serialized)
        verify(logTypedTransformer).transformOrNull(tag)
        verify(entityTransformer).transform(argThat { this is RuntimeException && this.message == "Unfair" })
        verifyNoMoreInteractions(entityTransformer)
        verifyNoMoreInteractions(logTypedTransformer)
        assertEquals(SerializedLogMessage.Error::class.java, serialized.message.javaClass)
    }
}