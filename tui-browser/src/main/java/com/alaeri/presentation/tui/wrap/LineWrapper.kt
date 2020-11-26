package com.alaeri.presentation.tui.wrap

import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiText
import com.googlecode.lanterna.SGR
import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.Label
import com.googlecode.lanterna.gui2.Panel

class LineWrapper{

    public fun printV2(
        contentStatus: LoadingStatus.Done,
        maxColumns: Int,
        maxHeight: Int,
        mainContentPanelLeft: Panel
    ) {
        val wrappedLines = contentStatus.result.lines.flatMap { line ->
            data class CharAndElement(val char: Char, val element: WikiText)
            data class LineChunkAndElement(val chars: String, val element: WikiText)

            val paragraphLines = line.flatMap { element ->
                element.text.toCharArray().asList().map { CharAndElement(it, element) }
            }.chunked(maxColumns).map { listChars ->
                listChars.fold(listOf<LineChunkAndElement>()) { acc: List<LineChunkAndElement>, charAndElement ->
                    if (acc.isEmpty()) {
                        listOf(
                            LineChunkAndElement(
                                charAndElement.char.toString(),
                                charAndElement.element
                            )
                        )
                    } else {
                        val last = acc.last()
                        if (last.element == charAndElement.element) {
                            acc.subList(
                                0,
                                acc.size - 1
                            ) + listOf(
                                LineChunkAndElement(
                                    last.chars + charAndElement.char,
                                    last.element
                                )
                            )
                        } else {
                            acc + listOf(
                                LineChunkAndElement(
                                    charAndElement.char.toString(),
                                    charAndElement.element
                                )
                            )
                        }
                    }
                }
            }
            paragraphLines
        }
        var countedButton = 0
        wrappedLines.forEachIndexed { rowIndex, line ->
            if (rowIndex < maxHeight) {
                line.fold(0) { colIndex, it ->
                    val label = Label(it.chars).apply {
                        position = TerminalPosition(colIndex, rowIndex)
                        size = TerminalSize(it.chars.length, 1)
                        if (it.element is WikiText.InternalLink) {
                            addStyle(SGR.BOLD)
                            countedButton++
                            addStyle(SGR.REVERSE)
                        }
                    }
                    mainContentPanelLeft.addComponent(label)
                    colIndex + it.chars.length
                }
            } else {
                //
            }
        }
    }

    public fun chunkAndWrapElements(
        line: MutableList<WikiText>,
        lineStartPos: TerminalPosition,
        maxColumns: Int
    ): AccChunks {
        return line.fold(AccChunks(lineStartPos, listOf())) { acc, wikiText ->
//            val startPosition = if (acc.lastPosition.row >= maxColumns) {
//                TerminalPosition(0, acc.lastPosition.row + 1)
//            } else {
//                acc.lastPosition
//            }
            val chunks = wrap(acc.lastPosition, wikiText, maxColumns)
            acc.copy(
                lastPosition = TerminalPosition(
                    chunks.last().end.column,
                    chunks.last().end.row
                ),
                chunks = acc.chunks + chunks
            )
        }
    }

    data class AccChunks(val lastPosition: TerminalPosition, val chunks: List<ChunkedTextLine>)

    private fun wrap(
        textStartPos: TerminalPosition,
        wikiText: WikiText,
        maxColumns: Int
    ): List<ChunkedTextLine> {
        val padLength = textStartPos.column
        val paddedText = "".padStart(padLength, '-') + wikiText.text
        //println(paddedText)
        return paddedText.chunked(maxColumns).mapIndexed { index, chunk ->
            val startCol = if (index == 0) {
                textStartPos.column
            } else {
                0
            }
            val unpaddeddChunk = if(index == 0){
                chunk.slice(padLength until chunk.length)
            }else{
                chunk.trimStart()
            }
            val mappedWikiText = when(wikiText){
                is WikiText.InternalLink -> WikiText.InternalLink(unpaddeddChunk, wikiText.target)
                else -> WikiText.NormalText(unpaddeddChunk)
            }
            val startPos = TerminalPosition(startCol, textStartPos.row + index)
            val endPos = TerminalPosition(startCol + unpaddeddChunk.length, startPos.row)
            ChunkedTextLine(mappedWikiText, startPos, endPos)
        }
    }
}