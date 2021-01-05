package com.alaeri.log.extra.tag.thread

import com.alaeri.log.extra.identity.IdOwner
import com.alaeri.log.extra.identity.IdentityRepresentation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("threadGroup")
data class ThreadGroupRepresentation(
    val name : String,
    val isDaemon : Boolean,
    val isDestroyed: Boolean,
    val activeCount : Int,
    val activeGroupCount: Int,
    val maxPriority: Int,
    val parent: ThreadGroupRepresentation?,
    override val identity: IdentityRepresentation
): IdOwner