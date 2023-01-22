package com.alaeri.seqdiag

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.alaeri.log.extra.tag.receiver.ReceiverTag
import com.alaeri.seqdiag.wiki.App
import com.alaeri.seqdiag.wiki.WikiScreen
import com.alaeri.seqdiag.wiki.WikiViewModel
import com.alaeri.seqdiag.wiki.data.WikiRepository


fun main() {
    val receiverTag = ReceiverTag(App)
    logBlocking("main", receiverTag) {
        application {
            val appCoroutineScope = rememberCoroutineScope()
            App.scope = appCoroutineScope
            App.vm = WikiViewModel(App.scope, WikiRepository())
            Window(onCloseRequest = ::exitApplication, title = "Wiki") {
                logBlocking("populate and run app window", receiverTag){
                    WikiScreen()
                }
            }
            Window(onCloseRequest = {}, title = "SeqDiag - Chronological") {
                debugScreen()
            }
        }
    }
}
