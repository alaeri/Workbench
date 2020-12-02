package com.alaeri.presentation.wiki

import com.alaeri.command.*
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.command.core.invoke
import com.alaeri.domain.wiki.WikiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class ResetSelectionOnNewNavigationUseCase(private val sharedCoroutineScope: CoroutineScope,
                                           private val selectablesUseCase: SelectablesUseCase,
                                           private val selectionRepository: SelectionRepository,
                                           private val iRootCommandLogger: DefaultIRootCommandLogger
): ICommandRootOwner{

    override val commandRoot: AnyCommandRoot = buildCommandRoot(this,
        "resetSelection",
        CommandNomenclature.Application.Start,
        iRootCommandLogger)

    init {
        invokeRootCommand<Unit>("START", CommandNomenclature.Application.Start){
            sharedCoroutineScope.launch {
                supervisorScope {
                    val flow = syncInvokeFlow { selectablesUseCase.selectablesFlowCommand }
                    flow.collect {
                        invoke{
                            selectionRepository.select(null)
                        }
                    }
                }
            }
        }

    }
}