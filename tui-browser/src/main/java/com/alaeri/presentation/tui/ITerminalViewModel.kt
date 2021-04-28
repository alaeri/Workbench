package com.alaeri.presentation.tui

import com.alaeri.presentation.PresentationState
import kotlinx.coroutines.flow.Flow

interface ITerminalViewModel{
    suspend fun startProcessingKeyStrokes()
    val screenState: Flow<PresentationState>
}