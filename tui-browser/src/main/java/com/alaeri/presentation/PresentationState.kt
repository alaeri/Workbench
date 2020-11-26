package com.alaeri.presentation

import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiText

sealed class PresentationState{
    data class Presentation(val inputState: InputState,
                            val contentStatus: LoadingStatus,
                            val selectedWikiText: WikiText.InternalLink?) : PresentationState()
    data class Exit(val logs: List<String>): PresentationState()

}

