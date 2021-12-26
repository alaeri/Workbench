package com.alaeri.log.extra.serializer.mapping

import com.alaeri.log.core.context.EmptyTag
import com.alaeri.log.core.context.ListTag
import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.serialize.serialize.Identity
import com.alaeri.log.serialize.serialize.SerializedTag
import com.alaeri.log.serialize.serialize.mapping.TypedTransformer
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Created by Emmanuel Requier on 18/12/2020.
 */
class TypedTransformerTest {

    private val typedTransformer = object : TypedTransformer<EmptyTag, SerializedTag>(EmptyTag::class){

        override fun transform(logData: EmptyTag): SerializedTag {
            return object : SerializedTag{
                override val identity =  IdentityRepresentation(1, "1")
            }
        }
    }

    private val spiedTypedTransformer = spy(typedTransformer)

    @Test
    fun `testMappingWithCompatibleLogContext`(){
        val emptyLogContext = EmptyTag()
        val representation = spiedTypedTransformer.transformOrNull(emptyLogContext)
        verify(spiedTypedTransformer).transform(emptyLogContext)
        assertNotNull(representation)
    }

    @Test
    fun `testMappingWithIncompatibleLogContext`(){
        val listLogContext = ListTag(listOf())
        val representation = spiedTypedTransformer.transformOrNull(listLogContext)
        verify(spiedTypedTransformer).transformOrNull(any())
        verifyNoMoreInteractions(spiedTypedTransformer)
        assertNull(representation)
    }
}