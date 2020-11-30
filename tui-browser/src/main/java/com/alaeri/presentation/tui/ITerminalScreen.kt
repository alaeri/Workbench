package com.alaeri.presentation.tui

import com.alaeri.command.core.flow.FlowCommand
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.input.KeyStroke
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface ITerminalScreen{
    val keyFlow: SharedFlow<KeyStroke>
    val sizeFlow: Flow<TerminalSize>

}