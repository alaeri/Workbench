package com.alaeri.domain.wiki

import com.alaeri.command.core.flow.FlowCommand
import kotlinx.coroutines.flow.Flow

interface WikiRepository{
    fun loadWikiArticle(searchTerm: String?): FlowCommand<LoadingStatus>
}

