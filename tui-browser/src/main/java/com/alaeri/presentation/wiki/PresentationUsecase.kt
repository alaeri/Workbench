package com.alaeri.presentation.wiki

import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiArticle
import com.alaeri.domain.wiki.WikiText
import com.alaeri.log
import com.alaeri.logBlocking
import com.alaeri.logFlow
import com.alaeri.presentation.ContentPanelState
import com.alaeri.presentation.InputState
import com.alaeri.presentation.PresentationState
import com.alaeri.presentation.tui.wrap.ChunkedTextLine
import com.alaeri.presentation.tui.wrap.LineWrapper
import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class ReflowUC(private val sizeFlow: Flow<TerminalSize>,
               private val contentFlow: Flow<LoadingStatus>,
               private val lineWrapper: LineWrapper,
){

    val reflowedFlow = combine(sizeFlow, contentFlow){ size, content ->
        println("reflowing with size: $size")
        val wikiArticle = when(content){
            is LoadingStatus.Done -> {
                content.result
            }
            else -> WikiArticle(null, null, mutableListOf(mutableListOf(WikiText.NormalText(content.toString()))))
        }
        val startPos = TerminalPosition(0, 0)
        val endPos = TerminalPosition(size.columns, size.rows)
        val maxColumns = size.columns
        val mutableChunks = mutableListOf<LineWrapper.AccChunks>()
        wikiArticle.lines.fold(startPos){ lineStartPos, line ->
            if (lineStartPos.row < endPos.row) {
                val result =
                    lineWrapper.chunkAndWrapElements(
                        line,
                        lineStartPos,
                        maxColumns
                    )
                mutableChunks.add(result)
                TerminalPosition(0, result.lastPosition.row + 1)
            }else{
                lineStartPos
            }
        }
        return@combine ContentPanelState(mutableChunks.toList(), size, 0)
    }
}
class PresentationUsecase(
    private val sharingScope: CoroutineScope,
    private val exitFlow: Flow<Boolean>,
    private val reflowedMainPanelContent: ReflowUC,
    private val selectionRepository: SelectionRepository,
    private val queryRepository: QueryRepository,
    private val pathRepository: PathRepository,
    private val onSelectionFetchPreviewUC: ReflowUC//OnSelectionFetchPreviewUC
){

    val presentationStateFlow : Flow<PresentationState> by lazy { logBlocking(name = "presentation state flow") {
            exitFlow
                .log("exitFlow")
                .flatMapLatest {
                if (it) {
                    flowOf(PresentationState.Exit(listOf()))
                } else {
                   combine(
                        reflowedMainPanelContent.reflowedFlow.log("leftPanelFlow"),
                        selectionRepository.selectionFlowCommand.log("selectionFlow"),
                        queryRepository.queryFlowCommand.log("queryFlow"),
                        pathRepository.pathFlow.log("pathFlow"),
                        onSelectionFetchPreviewUC.reflowedFlow.log("rightPanelFlow")
                    ) { loadingStatus, internalLink, query, path, previewStatus ->
                        log("combine"){
                            println("combine: $query")
                            PresentationState.Presentation(
                                InputState(query, path),
                                loadingStatus,
                                previewStatus,
                                internalLink,
                                TerminalSize(0, 0)
                            )
                        }
                    }
                }
                .log("combine")
            }.shareIn(sharingScope, replay = 1, started = SharingStarted.Lazily)
        }
    }

}