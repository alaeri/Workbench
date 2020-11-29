package com.alaeri.presentation.tui

import com.alaeri.command.core.flow.FlowCommand
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.input.KeyStroke
import kotlinx.coroutines.flow.Flow

interface ITerminalScreen{
    val keyFlow: FlowCommand<KeyStroke>
    val sizeFlow: Flow<TerminalSize>

}