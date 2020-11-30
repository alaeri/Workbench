package com.alaeri.presentation.wiki

import com.alaeri.domain.wiki.WikiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class ResetSelectionOnNewNavigationUseCase(private val sharedCoroutineScope: CoroutineScope,
                                           private val sharedSelectablesFlow: SharedFlow<List<WikiText.InternalLink>>,
                                           private val selectionRepository: SelectionRepository
){

    init {
        sharedCoroutineScope.launch {
            supervisorScope {
                sharedSelectablesFlow.collect {
                    selectionRepository.select(null)
                }
            }
        }
    }
}