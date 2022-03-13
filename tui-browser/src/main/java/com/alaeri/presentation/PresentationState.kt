package com.alaeri.presentation

import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiText
import com.alaeri.presentation.tui.wrap.ChunkedTextLine
import com.alaeri.presentation.tui.wrap.LineWrapper
import com.alaeri.presentation.wiki.PanelSizes
import com.alaeri.presentation.wiki.SelectionRepository
import com.googlecode.lanterna.TerminalSize

data class ContentPanelState(val lines: List<LineWrapper.AccChunks>,
                             val panelSize: TerminalSize,
                             val firstVisibleLine: Int)
sealed class PresentationState{
    data class Presentation(val inputState: InputState,
                            val contentStatus: ContentPanelState,
                            val previewStatus: ContentPanelState,
                            val selectedWikiText: SelectionRepository.Selection?,
                            val terminalSize: TerminalSize
    ) : PresentationState()
    data class Exit(val logs: List<String>): PresentationState()
    object Loading: PresentationState()

}

