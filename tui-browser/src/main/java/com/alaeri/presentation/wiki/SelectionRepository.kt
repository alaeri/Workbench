package com.alaeri.presentation.wiki

import com.alaeri.command.core.command
import com.alaeri.command.core.Command
import com.alaeri.command.core.flow.flowCommand
import com.alaeri.command.core.flow.shared
import com.alaeri.domain.wiki.WikiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

class SelectionRepository() {
    private val mutableSelectedWikiText = MutableStateFlow<WikiText.InternalLink?>(null)
    fun select(link: WikiText.InternalLink?): Command<Unit> = command(name ="select"){
        mutableSelectedWikiText.value = link
    }
    val selectionFlow: SharedFlow<WikiText.InternalLink?> = mutableSelectedWikiText
    val selectionFlowCommand = flowCommand<WikiText.InternalLink?>(name = "selection flow") { mutableSelectedWikiText }.shared()
}