package com.alaeri.seqdiag.wiki

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.input.TextFieldValue
import com.alaeri.log.extra.tag.receiver.ReceiverTag
import com.alaeri.seqdiag.log
import com.alaeri.seqdiag.logBlocking
import com.mikepenz.markdown.Markdown

@Composable
@Preview
fun WikiScreen(){

    val receiverTag = ReceiverTag(App)
    logBlocking("update screen", receiverTag) {
        val state: State<WikiViewModel.State> = App.vm.state.log("appState", ReceiverTag(
            App
        ))
            .collectAsState(WikiViewModel.State(TextFieldValue("test"), "##nanana"))
        Column(modifier = Modifier.fillMaxWidth()) {
            val uriHandler = object : UriHandler {
                override fun openUri(uri: String) {
                    App.vm.updateUri(uri)
                }
            }
            CompositionLocalProvider(LocalUriHandler.provides(uriHandler)){
                Markdown(state.value.markdown, modifier =Modifier.weight(1f, true))
            }



            TextField(modifier = Modifier.fillMaxWidth(),
                value = state.value.input,
                onValueChange = {
                    logBlocking("onValueChange", receiverTag) {
                        App.vm.setValue(it)
                    }
                })
        }
    }
}