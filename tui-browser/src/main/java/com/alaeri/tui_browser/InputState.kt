package com.alaeri.tui_browser

data class InputState(val text: String, val searchTerm : String? = null, val error: BrowserException? = null)