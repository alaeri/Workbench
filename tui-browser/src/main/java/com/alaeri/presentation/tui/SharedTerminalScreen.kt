package com.alaeri.presentation.tui

import com.alaeri.command.CommandNomenclature
import com.alaeri.command.core.ExecutableContext
import com.alaeri.command.core.ExecutionContext
import com.alaeri.command.core.flow.FlowCommand
import com.alaeri.command.core.flow.flowCommand
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

    inline fun <R> Any.sharedFlowCommand(sharedScope: CoroutineScope,
                                         name:String? = null,
                                         nomenclature: CommandNomenclature = CommandNomenclature.Undefined,
                                         crossinline op: ExecutionContext<R>.()->Flow<R>): FlowCommand<R> {
        val executionContext =
            ExecutableContext<R>(this)
        return FlowCommand<R>(
            this,
            nomenclature,
            name,
            executionContext
        ) { this@FlowCommand.op().shareIn(sharedScope, SharingStarted.Lazily) }
    }

    override val keyFlow: FlowCommand<KeyStroke> = sharedFlowCommand(initializationScope) {
        _keyFlow.shareIn(
            initializationScope,
            SharingStarted.Lazily
        )
    }

    override val sizeFlow: Flow<TerminalSize> = _sizeFlow.shareIn(initializationScope,
        SharingStarted.Lazily
    )

}