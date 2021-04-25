package com.alaeri.presentation.wiki

import com.alaeri.log
import kotlinx.coroutines.flow.firstOrNull

class NavigateToQueryUseCase(private val queryRepository: QueryRepository,
                             private val pathRepository: PathRepository
){
   suspend fun navigateToCurrentQuery(intent: Intent.NavigateToQuery) = log(name="navigate to query"){
       val query = queryRepository.queryFlow.firstOrNull()
       pathRepository.select(query)
       queryRepository.updateQuery("")
   }
}