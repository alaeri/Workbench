package com.alaeri.tui_browser

import kotlinx.coroutines.flow.Flow

interface ITerminalViewModel{
    val screenState: Flow<PresentationState>
}