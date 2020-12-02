package com.alaeri.presentation.wiki

import com.alaeri.command.*
import com.alaeri.command.core.flow.FlowCommand
import com.alaeri.command.core.flow.flowCommand
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.command.core.suspend.suspendInvokeFlow
import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class SelectablesUseCase(
    private val loadWikiOnPathUseCase: LoadWikiOnPathUseCase,
    private val sharedCoroutineScope: CoroutineScope,
    private val iRootCommandLogger: DefaultIRootCommandLogger
): ICommandRootOwner{
//    val selectablesFlow : SharedFlow<List<WikiText.InternalLink>> = loadWikiOnPathUseCase.loadingStatusFlow.map {
//        when(it){
//            is LoadingStatus.Done -> it.result.lines.flatMap { it.mapNotNull { it as? WikiText.InternalLink } }
//            else -> listOf<WikiText.InternalLink>()
//        }
//    }.stateIn(sharedCoroutineScope, started = SharingStarted.Lazily, initialValue = listOf())

    override val commandRoot: AnyCommandRoot = buildCommandRoot(this, "commandRootToExtract stuff", CommandNomenclature.Root, iRootCommandLogger)
    val loadingStatusFlow : Flow<LoadingStatus> = invokeRootCommand("loadinStatusFlow", CommandNomenclature.Application.Start){ syncInvokeFlow { loadWikiOnPathUseCase.loadingStatusInCommand } }

    val selectablesFlowCommand : FlowCommand<List<WikiText.InternalLink>> = flowCommand<List<WikiText.InternalLink>> {
        loadingStatusFlow.map {
                when(it){
                    is LoadingStatus.Done -> it.result.lines.flatMap { it.mapNotNull { it as? WikiText.InternalLink } }
                    else -> listOf<WikiText.InternalLink>()
                }
            }.shareIn(sharedCoroutineScope, replay = 1, started = SharingStarted.Lazily)
    }
}