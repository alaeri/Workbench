package com.alaeri.presentation.wiki

import com.alaeri.command.CommandState
import com.alaeri.command.core.command
import com.alaeri.command.core.Command
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.command.core.invoke
import com.alaeri.command.core.suspendInvoke
import com.alaeri.presentation.PresentationState
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first

class KeyStrokeToIntentUseCase(private val keyFlow: SharedFlow<KeyStroke>,
                               private val browsingService: BrowsingService,
                               private val coroutineScope: CoroutineScope
){

    fun start() : Command<Unit> = command {
        coroutineScope.launch {
            withContext(Dispatchers.Unconfined){
                keyFlow.collect { keyStroke ->
                    //println("test $keyStroke")
                    val presentationState = syncInvokeFlow { browsingService.presentationStateCommand }.first()
                    emit(CommandState.Update(presentationState))
                    emit(CommandState.Update(keyStroke))
                    //println("test2: $presentationState")
                    val intent: Intent = invoke { findIntentForKeyStroke(keyStroke, presentationState) }
                    suspendInvoke { browsingService.processIntent(intent) }
                }
            }
        }
    }

    private fun findIntentForKeyStroke(
        keyStroke: KeyStroke,
        presentationState: PresentationState
    ): Command<Intent> = command {
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
                            keyType == KeyType.Tab -> Intent.SelectNextLink
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
                                Intent.Edit(acc.text)
                            }
                            keyType == KeyType.ArrowRight -> Intent.NavigateToSelection
                            char != null && !char.isWhitespace() -> Intent.Edit(acc.text + char)
                            else -> Intent.Edit(acc.text)
                        }

                    }
                }
            }
        }
    }
}