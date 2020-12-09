package com.alaeri.presentation.wiki

import com.alaeri.command.*
import com.alaeri.command.core.flow.*
import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class SelectablesUseCase(
    private val loadWikiOnPathUseCase: LoadWikiOnPathUseCase,
    private val sharedCoroutineScope: CoroutineScope,
    private val iCommandLogger: ICommandLogger
){


    val selectablesFlowCommand : IFlowCommand<List<WikiText.InternalLink>> = flowCommand<List<WikiText.InternalLink>>(name = "selectables flow") {
        val loadingStatusFlow = syncInvokeFlow { loadWikiOnPathUseCase.loadingStatusInCommand }
        loadingStatusFlow.map {
                when(it){
                    is LoadingStatus.Done -> it.result.lines.flatMap { it.mapNotNull { it as? WikiText.InternalLink } }
                    else -> listOf<WikiText.InternalLink>()
                }
            }.distinctUntilChanged().shareIn(sharedCoroutineScope, replay = 1, started = SharingStarted.Lazily)
    }.shared()
}