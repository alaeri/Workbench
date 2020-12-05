package com.alaeri.domain.wiki

import com.alaeri.command.core.flow.FlowCommand
import com.alaeri.command.core.flow.IFlowCommand
import kotlinx.coroutines.flow.Flow

interface WikiRepository{
    fun loadWikiArticle(searchTerm: String?): Flow<LoadingStatus>
    fun loadWikiArticleCommand(searchTerm: String): IFlowCommand<LoadingStatus>
}

