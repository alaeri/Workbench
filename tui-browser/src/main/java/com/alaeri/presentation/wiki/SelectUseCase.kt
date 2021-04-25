package com.alaeri.presentation.wiki

import com.alaeri.log
import kotlinx.coroutines.flow.first


class SelectUseCase(private val selectablesUseCase: SelectablesUseCase,
                    private val selectionRepository: SelectionRepository
){

    suspend fun selectNextLink(intent: Intent.SelectNextLink) = log("select next link", intent) {
        val selectables = selectablesUseCase.selectablesFlow.first().mapIndexed{
            index, selectable -> SelectionRepository.Selection(index, selectable)
        }
        if(selectables.isNotEmpty()){
            val selection =  selectionRepository.selectionFlow.first()
            val newSelection = if(intent.forward){
                if(selection != null){
                    val index = selection.index
                    if(index >= 0){
                        selectables[(index+1)%selectables.size]
                    }else{
                        selectables.firstOrNull()
                    }
                }else{
                    selectables.firstOrNull()
                }
            }else{
                if(selection != null){
                    val index = selectables.indexOf(selection)
                    if(index > 0){
                        selectables[(index-1)%selectables.size]
                    }else{
                        selectables.lastOrNull()
                    }
                }else{
                    selectables.lastOrNull()
                }
            }
            selectionRepository.select(newSelection)
        }
    }
}