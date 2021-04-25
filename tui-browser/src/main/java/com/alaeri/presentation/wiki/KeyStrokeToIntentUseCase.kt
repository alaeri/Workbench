package com.alaeri.presentation.wiki

import com.alaeri.log
import com.alaeri.logBlocking
import com.alaeri.presentation.PresentationState
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KeyStrokeToIntentUseCase(private val keyFlow: SharedFlow<KeyStroke>,
                               private val browsingService: BrowsingService,
                               private val coroutineScope: CoroutineScope
){

    fun start() : Unit = logBlocking(name ="start processing keystrokes") {
        coroutineScope.launch {
            withContext(Dispatchers.Unconfined){
                keyFlow.collect { keyStroke ->
                    processKeyStroke(keyStroke)

                }
            }
        }
    }

    private suspend fun processKeyStroke(keyStroke: KeyStroke) : Unit = log(name = "process keystroke") {
        val presentationState =  browsingService.presentationState.first()
        val intent: Intent = findIntentForKeyStroke(keyStroke, presentationState)
        browsingService.processIntent(intent)
    }

    private fun findIntentForKeyStroke(
        keyStroke: KeyStroke,
        presentationState: PresentationState
    ): Intent = logBlocking("findintent") {
        val char = keyStroke.character
        val keyType = keyStroke.keyType
        when {
            keyType == KeyType.EOF || keyType == KeyType.Escape -> Intent.Exit
            else -> {
                when (presentationState) {
                    is PresentationState.Exit -> Intent.Exit
                    is PresentationState.Presentation -> {
                        val acc = presentationState.inputState
                        val currentQueryLength = acc.text.length
                        when {
                            keyType == KeyType.ArrowLeft -> Intent.SelectNextLink(forward = false)
                            keyType == KeyType.ArrowRight -> Intent.SelectNextLink()
                            keyType == KeyType.Backspace -> if (currentQueryLength > 0) {
                                val slicedText =
                                    acc.text.slice(0 until currentQueryLength - 1)
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
                            char != null && !char.isWhitespace() -> Intent.Edit(acc.text + char)
                            else -> Intent.Edit(acc.text)
                        }
                    }
                }
            }
        }
    }
}