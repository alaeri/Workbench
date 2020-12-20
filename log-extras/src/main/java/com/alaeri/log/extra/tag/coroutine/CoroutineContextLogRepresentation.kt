package com.alaeri.log.extra.tag.coroutine

import com.alaeri.log.extra.identity.IdentityRepresentation

/**
 * Created by Emmanuel Requier on 19/12/2020.
 */
data class CoroutineContextLogRepresentation(val id: IdentityRepresentation,
                                             val isActive: Boolean,
                                             val job: JobRepresentation)