package com.alaeri.presentation.wiki

import com.alaeri.command.*
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.command.core.invoke
import com.alaeri.command.core.root.DefaultRootCommandScope
import com.alaeri.command.core.root.ICommandScopeOwner
import com.alaeri.command.core.root.buildRootCommandScope
import com.alaeri.command.core.root.invokeRootCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class ResetSelectionOnNewNavigationUseCase(private val sharedCoroutineScope: CoroutineScope,
                                           private val selectablesUseCase: SelectablesUseCase,
                                           private val selectionRepository: SelectionRepository,
                                           private val iCommandLogger: ICommandLogger
): ICommandScopeOwner{

    override val commandScope: DefaultRootCommandScope = buildRootCommandScope(this,
        "resetSelection",
        CommandNomenclature.Application.Start,
        iCommandLogger)

    init {
        invokeRootCommand<Unit>("reset selection on new path", CommandNomenclature.Application.Start){
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