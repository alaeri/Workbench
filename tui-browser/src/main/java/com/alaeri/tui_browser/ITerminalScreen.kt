package com.alaeri.tui_browser

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.input.KeyStroke
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

interface ITerminalScreen{
    val keyFlow: Flow<KeyStroke>
    val sizeFlow: Flow<TerminalSize>

}