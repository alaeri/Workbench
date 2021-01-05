package com.alaeri.log.serialize.serialize.mapping

import com.alaeri.log.core.child.ChildTag
import com.alaeri.log.core.context.ListTag
import com.alaeri.log.core.Log.Tag
import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.serialize.serialize.EmptySerializedTag
import com.alaeri.log.serialize.serialize.IIdentityTransformer
import com.alaeri.log.serialize.serialize.Identity
import com.alaeri.log.serialize.serialize.SerializedTag
import com.alaeri.log.serialize.serialize.representation.FiliationRepresentation
import com.alaeri.log.serialize.serialize.representation.ListRepresentation

class CombinedTagTransformer(
    private val innerTypedMappers: List<TagTypedSerializer< *, *>>,
    private val identityTransformer: IIdentityTransformer<IdentityRepresentation>
):
    TagTypedSerializer<Tag, SerializedTag>(Tag::class) {

    override fun transform(tag: Tag) : SerializedTag {
        return when(tag){
            is ListTag -> ListRepresentation(tag.list.map { transform(it) }, identityTransformer.transform(tag) )
            is ChildTag -> FiliationRepresentation(transform(tag.parentTag), identityTransformer.transform(tag)) as SerializedTag
            else -> {
                val list = innerTypedMappers.mapNotNull {
                    it.transformOrNull(tag) as? SerializedTag
                }
                when {
                    list.size>1 -> {
                        ListRepresentation(list, identityTransformer.transform(tag))
                    }
                    list.isNotEmpty() -> {
                        list.first()
                    }
                    else -> {
                        println(tag)
                        EmptySerializedTag(identityTransformer.transform(tag))
                    }
                }
            }
        }
    }
}