package com.alaeri.presentation.wiki

import com.alaeri.log
import com.alaeri.logBlocking
import com.alaeri.presentation.PresentationState
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class KeyStrokeToIntentUseCase(private val keyFlow: SharedFlow<KeyStroke>,
                               private val browsingService: BrowsingService,
                               private val queryRepository: QueryRepository,
                               private val processKeyStrokeCoroutineContext: CoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
){

    suspend fun start() : Unit = log(name ="start processing keystrokes") {
        withContext(processKeyStrokeCoroutineContext){
            keyFlow.buffer(10).collect { keyStroke ->
                processKeyStroke(keyStroke)
            }
        }
    }

    private suspend fun processKeyStroke(keyStroke: KeyStroke) : Unit = log(name = "process keystroke") {
        val currentQuery =  queryRepository.queryFlow.first()
        val intent: Intent = findIntentForKeyStroke(keyStroke, currentQuery)
        browsingService.processIntent(intent)
    }

    private fun findIntentForKeyStroke(
        keyStroke: KeyStroke,
        currentQuery: String
    ): Intent = logBlocking("findintent") {
        val char = keyStroke.character
        val keyType = keyStroke.keyType
        when {
            keyType == KeyType.EOF || keyType == KeyType.Escape -> Intent.Exit
            else -> {
//                when (presentationState) {
//                    is PresentationState.Exit -> Intent.Exit
//                    is PresentationState.Presentation -> {

                val currentQueryLength = currentQuery.length
                when {
                    keyType == KeyType.ArrowLeft -> Intent.SelectNextLink(forward = false)
                    keyType == KeyType.ArrowRight -> Intent.SelectNextLink()
                    keyType == KeyType.Backspace -> if (currentQueryLength > 0) {
                        val slicedText =
                            currentQuery.slice(0 until currentQueryLength - 1)
                        Intent.Edit(slicedText)
                    } else {
                        Intent.Edit("")
                    }
                    keyType == KeyType.Enter -> if (currentQueryLength > 0) {
                        Intent.NavigateToQuery
                    } else {
                        Intent.NavigateToSelection
                    }
                    keyType == KeyType.ArrowDown -> Intent.NavigateToSelection
                    keyType == KeyType.Escape -> Intent.ClearSelection
                    keyType == KeyType.Tab -> Intent.ChangeSelectedTab
                    char != null && !char.isWhitespace() -> Intent.Edit(currentQuery + char)
                    else -> Intent.Edit(currentQuery)
                }
            }
        }
    }
}