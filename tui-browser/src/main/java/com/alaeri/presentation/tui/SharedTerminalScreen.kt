package com.alaeri.presentation.tui

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.input.KeyStroke
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

class SharedTerminalScreen(_keyFlow: Flow<KeyStroke>,
                           _sizeFlow: Flow<TerminalSize>,
                           initializationScope: CoroutineScope
): ITerminalScreen {

    override val keyFlow: Flow<KeyStroke> = _keyFlow.shareIn(initializationScope,
        SharingStarted.Lazily
    )
    override val sizeFlow: Flow<TerminalSize> = _sizeFlow.shareIn(initializationScope,
        SharingStarted.Lazily
    )

}