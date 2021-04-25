package com.alaeri.presentation

import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiText
import com.alaeri.presentation.wiki.SelectionRepository

sealed class PresentationState{
    data class Presentation(val inputState: InputState,
                            val contentStatus: LoadingStatus,
                            val previewStatus: LoadingStatus,
                            val selectedWikiText: SelectionRepository.Selection?) : PresentationState()
    data class Exit(val logs: List<String>): PresentationState()

}

