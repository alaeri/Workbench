package com.alaeri.tui_browser

import com.alaeri.tui_browser.wiki.LoadingStatus
import com.alaeri.tui_browser.wiki.WikiText

sealed class PresentationState{
    data class Presentation(val inputState: InputState,
                            val contentStatus: LoadingStatus,
                            val selectedWikiText: WikiText.InternalLink?) : PresentationState()
    data class Exit(val logs: List<String>): PresentationState()

}

