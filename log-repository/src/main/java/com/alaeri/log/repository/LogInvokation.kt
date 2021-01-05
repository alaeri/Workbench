package com.alaeri.log.repository

import com.alaeri.log.extra.identity.IdentityRepresentation

data class CommandInvokation(
    val tagId: IdentityRepresentation,
    val relations : List<IdentityRepresentation>
)