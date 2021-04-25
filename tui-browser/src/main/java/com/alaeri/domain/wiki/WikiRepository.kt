package com.alaeri.domain.wiki

import kotlinx.coroutines.flow.Flow

interface WikiRepository{
    fun loadWikiArticle(searchTerm: String?): Flow<LoadingStatus>
}

