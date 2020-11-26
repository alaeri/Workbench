package com.alaeri.presentation

import com.alaeri.domain.wiki.BrowserException

data class InputState(val text: String, val searchTerm : String? = null, val error: BrowserException? = null)