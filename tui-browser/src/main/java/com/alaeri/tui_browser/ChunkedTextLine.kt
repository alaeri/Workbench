package com.alaeri.tui_browser

import com.alaeri.tui_browser.wiki.WikiText
import com.googlecode.lanterna.TerminalPosition

data class ChunkedTextLine(
    val text: WikiText,
    val position: TerminalPosition,
    val end: TerminalPosition
)