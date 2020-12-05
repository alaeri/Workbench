package com.alaeri.presentation.tui

import com.alaeri.command.core.flow.FlowCommand
import com.alaeri.command.core.flow.IFlowCommand
import com.alaeri.presentation.PresentationState
import kotlinx.coroutines.flow.Flow

interface ITerminalViewModel{
    //val screenState: Flow<PresentationState>
    val screenStateCommand: IFlowCommand<PresentationState>
}