package com.alaeri.log.extra.tag.coroutine

import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.serialize.serialize.SerializedTag

/**
 * Created by Emmanuel Requier on 19/12/2020.
 */
data class CoroutineContextLogRepresentation(override val identity: IdentityRepresentation,
                                             val isActive: Boolean,
                                             val job: JobRepresentation?): SerializedTag