package com.alaeri.log.repository

import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.extra.tag.name.NameRepresentation
import com.alaeri.log.extra.tag.receiver.ReceiverRepresentation

data class LogGroupId(
    val name: NameRepresentation,
    val receiver: ReceiverRepresentation,
)

