package com.alaeri.presentation.tui.wrap

import com.alaeri.domain.wiki.WikiText
import com.googlecode.lanterna.TerminalPosition

data class ChunkedTextLine(
    val text: WikiText,
    val position: TerminalPosition,
    val end: TerminalPosition
)