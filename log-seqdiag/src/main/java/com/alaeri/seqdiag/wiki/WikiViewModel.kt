package com.alaeri.seqdiag.wiki

import androidx.compose.ui.text.input.TextFieldValue
import com.alaeri.log.extra.tag.receiver.ReceiverTag
import com.alaeri.seqdiag.log
import com.alaeri.seqdiag.logBlocking
import com.alaeri.seqdiag.logShareIn
import com.alaeri.seqdiag.wiki.data.WikiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class WikiViewModel(private val sharingScope: CoroutineScope,
                    private val wikiRepository: WikiRepository) {

    private val receiverTag = ReceiverTag(this)

    private val inputMutableStateFlow : MutableStateFlow<TextFieldValue> = MutableStateFlow(
        TextFieldValue("test")
    )

    fun setValue(textFieldValue: TextFieldValue){
        logBlocking("changeInput", receiverTag) {
            inputMutableStateFlow.value = textFieldValue
        }
    }

    fun updateUri(uri: String) {
        logBlocking("changeUri", receiverTag) {
            inputMutableStateFlow.value = inputMutableStateFlow.value.copy(text = uri)
        }
    }

    private val inputFlow = inputMutableStateFlow.log("inputFlow", receiverTag)

    private val wikiPageFlow = inputFlow
        .map { it.text }
        .distinctUntilChanged()
        .flatMapLatest {
            log("loadWikiPage", receiverTag) {
                wikiRepository.loadWikiArticle(it).map { it.toMarkdown() }
            }
        }
        .log("wikiPageFlow", receiverTag)
        .flowOn(Dispatchers.IO)
        .distinctUntilChanged()

    data class State(val input: TextFieldValue, val markdown: String)
    val state = combine(inputFlow, wikiPageFlow) { e, t -> State(e, t) }
        .logShareIn(
            name = "sharedState",
            receiverTag = receiverTag,
            coroutineScope = sharingScope,
            sharingStarted = SharingStarted.Eagerly,
            replayCount = 1
        )
}