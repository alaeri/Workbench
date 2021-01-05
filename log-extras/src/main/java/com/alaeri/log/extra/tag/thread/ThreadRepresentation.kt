package com.alaeri.log.extra.tag.thread

import com.alaeri.log.extra.identity.IdOwner
import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.serialize.serialize.Identity
import com.alaeri.log.serialize.serialize.SerializedTag
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("thread")
data class ThreadRepresentation(val name: String,
                                val id: Long,
                                val isDaemon: Boolean,
                                val isAlive:Boolean,
                                val priority: Int,
                                val threadGroup: ThreadGroupRepresentation?,
                                override val identity: IdentityRepresentation
): SerializedTag, IdOwner{
}
