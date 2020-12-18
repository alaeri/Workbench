package com.alaeri.log.extra.serializer.mapping

import com.alaeri.log.core.context.EmptyLogContext
import com.alaeri.log.core.context.ListLogContext
import com.alaeri.log.extra.serialize.LogRepresentation
import com.alaeri.log.extra.serialize.mapping.TypedTransformer
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Created by Emmanuel Requier on 18/12/2020.
 */
class TypedTransformerTest {

    private val typedTransformer = object : TypedTransformer<EmptyLogContext, LogRepresentation<EmptyLogContext>>(EmptyLogContext::class){

        override fun transform(logData: EmptyLogContext): LogRepresentation<EmptyLogContext> {
            return object : LogRepresentation<EmptyLogContext>{}
        }
    }

    private val spiedTypedTransformer = spy(typedTransformer)

    @Test
    fun `testMappingWithCompatibleLogContext`(){
        val emptyLogContext = EmptyLogContext()
        val representation = spiedTypedTransformer.transformOrNull(emptyLogContext)
        verify(spiedTypedTransformer).transform(emptyLogContext)
        assertNotNull(representation)
    }

    @Test
    fun `testMappingWithIncompatibleLogContext`(){
        val listLogContext = ListLogContext(listOf())
        val representation = spiedTypedTransformer.transformOrNull(listLogContext)
        verify(spiedTypedTransformer).transformOrNull(any())
        verifyNoMoreInteractions(spiedTypedTransformer)
        assertNull(representation)
    }
}