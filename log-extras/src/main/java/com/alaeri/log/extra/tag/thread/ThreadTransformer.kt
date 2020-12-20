package com.alaeri.log.extra.tag.thread

import com.alaeri.log.extra.identity.IdentityTransformer
import com.alaeri.log.serialize.serialize.mapping.TypedTransformer

/**
 * Created by Emmanuel Requier on 19/12/2020.
 */
class ThreadTransformer(private val identityTransformer: IdentityTransformer): TypedTransformer<ThreadTag, ThreadRepresentation>(ThreadTag::class) {
    override fun transform(logData: ThreadTag): ThreadRepresentation {
        val thread = logData.thread
        val threadGroup = thread.threadGroup
        val threadGroupRepresentation = threadGroupRepresentation(threadGroup)
        val threadId = identityTransformer.transform(thread)
        return ThreadRepresentation(
            thread.name,
            thread.id,
            thread.isDaemon,
            thread.isAlive,
            thread.priority,
            threadGroupRepresentation,
            threadId
        )
    }

    private fun threadGroupRepresentation(threadGroup: ThreadGroup) : ThreadGroupRepresentation {
        val threadGroupId = identityTransformer.transform(threadGroup)
        return ThreadGroupRepresentation(
            threadGroup.name,
            threadGroup.isDaemon,
            threadGroup.isDestroyed,
            threadGroup.activeCount(),
            threadGroup.activeGroupCount(),
            threadGroup.maxPriority,
            null,//threadGroupRepresentation(threadGroup.parent),
            threadGroupId
        )
    }
}