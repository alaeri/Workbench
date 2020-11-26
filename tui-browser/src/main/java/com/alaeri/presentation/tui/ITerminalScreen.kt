package com.alaeri.presentation.tui

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.input.KeyStroke
import kotlinx.coroutines.flow.Flow

interface ITerminalScreen{
    val keyFlow: Flow<KeyStroke>
    val sizeFlow: Flow<TerminalSize>

}