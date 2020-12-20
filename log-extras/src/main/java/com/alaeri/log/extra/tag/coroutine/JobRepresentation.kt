package com.alaeri.log.extra.tag.coroutine

import com.alaeri.log.extra.identity.IdOwner
import com.alaeri.log.extra.identity.IdentityRepresentation

/**
 * Created by Emmanuel Requier on 19/12/2020.
 */
data class JobRepresentation(override val identity: IdentityRepresentation,
                             val isActive : Boolean,
                             val isCancelled: Boolean,
                             val isCompleted: Boolean,
                             val children: Sequence<JobRepresentation>): IdOwner