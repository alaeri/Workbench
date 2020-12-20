package com.alaeri.domain.wiki

import com.beust.klaxon.Json

data class ApiWikiArticle(@Json(path = "$.query.pages[0].revisions[0].slots.main.content") val content: String)