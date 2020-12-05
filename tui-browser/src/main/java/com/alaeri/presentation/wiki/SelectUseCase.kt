package com.alaeri.presentation.wiki

import com.alaeri.command.CommandState
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.invoke
import com.alaeri.command.core.suspend.suspendInvokeFlow
import com.alaeri.command.core.suspend.suspendingCommand
import com.alaeri.domain.wiki.WikiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first

class SelectUseCase(private val selectablesUseCase: SelectablesUseCase,
                    private val selectionRepository: SelectionRepository
){

    suspend fun selectNextLink(intent: Intent.SelectNextLink): SuspendingCommand<Unit> = suspendingCommand("select next link") {
        emit(CommandState.Update(intent))
        val selectables = syncInvokeFlow { selectablesUseCase.selectablesFlowCommand }.first() //syncInvokeFlow { selectablesUseCase.selectablesFlowCommand }.first()
        if(selectables.isNotEmpty()){
            val selection =  //selectionRepository.selectionFlow.first()
                syncInvokeFlow { selectionRepository.selectionFlowCommand }.first()
            val newSelection = if(selection != null){
                val index = selectables.indexOf(selection)
                if(index >= 0){
                    selectables[(index+1)%selectables.size]
                }else{
                    selectables.firstOrNull()
                }
            }else{
                selectables.firstOrNull()
            }
            invoke {
                selectionRepository.select(newSelection)
            }
        }
    }
}