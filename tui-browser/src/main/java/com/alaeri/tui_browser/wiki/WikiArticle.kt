package com.alaeri.tui_browser.wiki

data class WikiArticle(val about: String?,
                       val shortDescription: String?,
                       val lines: MutableList<MutableList<WikiText>>)