package com.alaeri.presentation.tui

import com.alaeri.presentation.wiki.PanelSizes
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.input.KeyStroke
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface ITerminalScreen{
    val keyFlow: Flow<KeyStroke>
    val sizeFlow: Flow<PanelSizes>

}