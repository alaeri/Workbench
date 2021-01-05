package com.alaeri.log.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created by Emmanuel Requier on 29/12/2020.
 */
class GraphRepository(private val logRepository: LogRepository) {
    val graph: Flow<GraphRepresentation> = logRepository.listAsFlow.map {
        GroupedLogsGraphMapper.mapToGraph(it)
    }
}
