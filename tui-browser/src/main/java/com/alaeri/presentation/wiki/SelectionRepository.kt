package com.alaeri.presentation.wiki

import com.alaeri.domain.wiki.WikiText
import com.alaeri.logBlocking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.Flow

class SelectionRepository() {

    data class Selection(val index: Int, val content: WikiText.InternalLink)

    private val mutableSelectedWikiText = MutableStateFlow<Selection?>(null)
    fun select(link: Selection?) = logBlocking(name ="select"){
        mutableSelectedWikiText.value = link
    }
    val selectionFlow: SharedFlow<Selection?> = mutableSelectedWikiText
    val selectionFlowCommand : Flow<Selection?>
        get()= logBlocking(name = "selection flow") { mutableSelectedWikiText }
}