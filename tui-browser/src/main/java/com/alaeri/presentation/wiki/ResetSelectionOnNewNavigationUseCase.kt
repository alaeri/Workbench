package com.alaeri.presentation.wiki

import com.alaeri.logBlocking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class ResetSelectionOnNewNavigationUseCase(private val sharedCoroutineScope: CoroutineScope,
                                           private val selectablesUseCase: SelectablesUseCase,
                                           private val selectionRepository: SelectionRepository
){

    init {
        logBlocking("reset selection on new path"){
            sharedCoroutineScope.launch {
                supervisorScope {
                    val flow = selectablesUseCase.selectablesFlow
                    flow.collect {
                        selectionRepository.select(null)
                    }
                }
            }
        }

    }
}