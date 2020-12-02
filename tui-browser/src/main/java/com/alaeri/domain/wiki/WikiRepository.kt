package com.alaeri.domain.wiki

import com.alaeri.command.core.flow.FlowCommand
import kotlinx.coroutines.flow.Flow

interface WikiRepository{
    fun loadWikiArticle(searchTerm: String?): Flow<LoadingStatus>
    fun loadWikiArticleCommand(searchTerm: String): FlowCommand<LoadingStatus>
}

