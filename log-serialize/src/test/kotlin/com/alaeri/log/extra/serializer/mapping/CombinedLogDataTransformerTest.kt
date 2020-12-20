package com.alaeri.log.extra.serializer.mapping

import com.alaeri.log.core.child.ChildTag
import com.alaeri.log.core.context.ListTag
import com.alaeri.log.core.Log.Tag
import com.alaeri.log.serialize.serialize.EmptySerializedTag
import com.alaeri.log.serialize.serialize.SerializedTag
import com.alaeri.log.serialize.serialize.mapping.CombinedLogDataTransformer
import com.alaeri.log.serialize.serialize.mapping.TagTypedSerializer
import com.alaeri.log.serialize.serialize.representation.FiliationRepresentation
import com.alaeri.log.serialize.serialize.representation.ListRepresentation
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
        val emptyMock: Tag = mock {  }
        val childLogContext : ChildTag = mock { on{parentTag}doReturn emptyMock }
        val childLogRepresentation = combinedLogDataTransformer.transform(childLogContext)
        assertEquals(FiliationRepresentation::class.java, childLogRepresentation.javaClass)
    }

    @Test
    fun `test list mapping`(){
        val combinedLogDataTransformer = CombinedLogDataTransformer(listOf())
        val emptyMock: Tag = mock {  }
        val listLogContext : ListTag = mock { on{list}doReturn listOf(emptyMock) }
        val listLogRepresentation = combinedLogDataTransformer.transform(listLogContext)
        assertEquals(ListRepresentation::class.java, listLogRepresentation.javaClass)
    }

    @Test
    fun `test inner mapping works`(){
        val emptyLogRepresentation = EmptySerializedTag()
        val logTypedMapper : TagTypedSerializer<Tag, SerializedTag<Tag>> = mock {
            onBlocking{transform(any())} doReturn emptyLogRepresentation
            onBlocking{transformOrNull(any())} doReturn emptyLogRepresentation
        }
        val combinedLogDataTransformer = CombinedLogDataTransformer(listOf(logTypedMapper))
        val tag: Tag = mock { }
        val mapped = combinedLogDataTransformer.transform(tag)
        verify(logTypedMapper).transformOrNull(eq(tag))
        assertEquals(ListRepresentation(listOf(emptyLogRepresentation)), mapped)
    }


}