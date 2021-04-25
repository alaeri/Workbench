package com.alaeri.presentation.wiki

import com.alaeri.log
import kotlinx.coroutines.flow.firstOrNull

class NavigateToSelectionUseCase(private val selectionRepository: SelectionRepository, private val pathRepository: PathRepository){

    suspend fun navigateToCurrentSelection(intent: Intent.NavigateToSelection) = log(name = "navigate to selection") {
        val selection = selectionRepository.selectionFlowCommand.firstOrNull()
        if(selection != null){
            pathRepository.select(selection.content.target)
        }
    }
}