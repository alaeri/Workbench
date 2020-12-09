package com.alaeri.presentation.wiki

import com.alaeri.command.core.command
import com.alaeri.command.core.Command
import com.alaeri.command.core.flow.FlowCommand
import com.alaeri.command.core.flow.IFlowCommand
import com.alaeri.command.core.flow.flowCommand
import com.alaeri.command.core.flow.shared
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

class PathRepository(){
    private val mutablePath = MutableStateFlow<String?>(null)
    suspend fun select(link: String?): Command<Unit> = command(name = "set path"){
        mutablePath.value = link
    }
    val pathFlow: SharedFlow<String?> = mutablePath
    val pathFlowCommand: IFlowCommand<String?> = flowCommand<String?>(name = "path flow") {
        pathFlow
    }.shared()

}