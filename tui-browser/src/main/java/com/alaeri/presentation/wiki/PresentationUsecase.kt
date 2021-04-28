package com.alaeri.presentation.wiki

import com.alaeri.log
import com.alaeri.logBlocking
import com.alaeri.logFlow
import com.alaeri.presentation.InputState
import com.alaeri.presentation.PresentationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class PresentationUsecase(
    val sharingScope: CoroutineScope,
    val exitFlow: Flow<Boolean>,
    val loadWikiOnPathUseCase: LoadWikiOnPathUseCase,
    val selectionRepository: SelectionRepository,
    val queryRepository: QueryRepository,
    val pathRepository: PathRepository,
    onSelectionFetchPreviewUC: OnSelectionFetchPreviewUC
){

    val presentationStateFlow : Flow<PresentationState> by lazy { logBlocking(name = "presentation state flow") {
            exitFlow
                .log("exitFlow")
                .flatMapLatest {
                if (it) {
                    flowOf(PresentationState.Exit(listOf()))
                } else {
                   combine(
                        loadWikiOnPathUseCase.loadingStatus.log("loadWiki"),
                        selectionRepository.selectionFlowCommand.log("selection"),
                        queryRepository.queryFlowCommand.log("query"),
                        pathRepository.pathFlow.log("path"),
                        onSelectionFetchPreviewUC.selectionPreview.log("selection")
                    ) { loadingStatus, internalLink, query, path, previewStatus ->
                        //println("combine.....")
                        PresentationState.Presentation(
                            InputState(query, path),
                            loadingStatus,
                            previewStatus,
                            internalLink
                        )
                    }
                }
                .log("combine")
            }.shareIn(sharingScope, replay = 1, started = SharingStarted.Lazily)
        }
    }

}