package com.alaeri.log.extra.tag.coroutine

import com.alaeri.log.extra.identity.IdentityTransformer
import com.alaeri.log.serialize.serialize.mapping.TagTypedSerializer
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive

/**
 * Created by Emmanuel Requier on 05/01/2021.
 */
class CoroutineContextSerializer(private val identityTransformer: IdentityTransformer): TagTypedSerializer<CoroutineContextTag, CoroutineContextLogRepresentation>(CoroutineContextTag::class){
    override fun transform(logData: CoroutineContextTag): CoroutineContextLogRepresentation {
        return CoroutineContextLogRepresentation(
            identity = identityTransformer.transform(logData.coroutineContext),
            isActive = logData.coroutineContext.isActive,
            job = logData.job?.representation()
        )
    }

    private fun Job.representation() : JobRepresentation = JobRepresentation(
        identityTransformer.transform(this),
        isActive = isActive,
        isCancelled = isCancelled,
        isCompleted = isCompleted,
        children = children.toList().map { it.representation() }.asSequence()
    )

}