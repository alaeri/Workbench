package com.alaeri.tui_browser.wiki

sealed class WikiText(open val text: String){

    data class NormalText(override val text: String): WikiText(text)
    data class InternalLink(override val text: String): WikiText(text)
}