package com.alaeri.presentation.wiki

import kotlinx.coroutines.flow.firstOrNull

class NavigateToSelectionUseCase(private val selectionRepository: SelectionRepository, private val pathRepository: PathRepository){

    suspend fun navigateToCurrentSelection(intent: Intent.NavigateToSelection){
        val selection = selectionRepository.selectionFlow.firstOrNull()
        if(selection != null){
            pathRepository.select(selection.target)
        }
    }
}