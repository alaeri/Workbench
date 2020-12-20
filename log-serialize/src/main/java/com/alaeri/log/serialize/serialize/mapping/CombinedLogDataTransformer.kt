package com.alaeri.log.serialize.serialize.mapping

import com.alaeri.log.core.child.ChildTag
import com.alaeri.log.core.context.ListTag
import com.alaeri.log.core.Log.Tag
import com.alaeri.log.serialize.serialize.SerializedTag
import com.alaeri.log.serialize.serialize.representation.FiliationRepresentation
import com.alaeri.log.serialize.serialize.representation.ListRepresentation

class CombinedLogDataTransformer(private val innerTypedMappers: List<TagTypedSerializer<*, *>>):
    TypedTransformer<Tag, SerializedTag<Tag>>(Tag::class) {

    override fun transform(tag: Tag) : SerializedTag<Tag> {
        return when(tag){
            is ListTag -> ListRepresentation(tag.list.map { transform(it) })
            is ChildTag -> FiliationRepresentation(transform(tag.parentTag)) as SerializedTag<Tag>
            else -> {
                val list = innerTypedMappers.mapNotNull {
                    it.transformOrNull(tag)
                }
                ListRepresentation(list)
            }
        }
    }
}