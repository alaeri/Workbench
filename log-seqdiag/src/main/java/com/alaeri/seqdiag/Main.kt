package com.alaeri.seqdiag

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
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
            Window(onCloseRequest = ::exitApplication, title = "SeqDiag Compose test") {
                logBlocking("populate and run app window", receiverTag){

                    println(Thread.currentThread().name)

                        WikiScreen()


                }

            }
            Window(onCloseRequest = {}, title = "SeqDiag Compose test") {
                logBlocking("populate and run app window", receiverTag){
                    println(Thread.currentThread().name)
                    debugScreen()
                }
            }
        }
    }
}
