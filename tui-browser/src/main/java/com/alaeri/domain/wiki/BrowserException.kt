package com.alaeri.domain.wiki

import com.alaeri.presentation.InputState
import com.googlecode.lanterna.input.KeyStroke

sealed class BrowserException(message: String, cause: Exception? = null): Exception(message, cause){
    data class InvalidInput(val keyStroke: KeyStroke, val state: InputState): BrowserException("invalid input $keyStroke for state: $state")
    object CaretAtStart: BrowserException("stop pressing on backspace it's useless")
    object NothingToSearch: BrowserException("nothing to search")
}