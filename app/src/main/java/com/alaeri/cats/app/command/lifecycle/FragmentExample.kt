package com.alaeri.cats.app.command.lifecycle

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.alaeri.command.CommandState
import com.alaeri.command.android.LifecycleCommandOwner
import com.alaeri.command.core.ICommandLogger
import com.alaeri.command.core.command
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent

@ExperimentalCoroutinesApi
class FragmentExample: Fragment(), LifecycleCommandOwner, KoinComponent {

    private val mutableStateFlow= MutableStateFlow<ICommandLogger<Any>?>(null)
    override val commandContext = buildLifecycleCommandContext(mutableStateFlow)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        commandContext.invokeLifecycleCommand<Unit> {
            emit(CommandState.Update("coucou"))
        }
        lifecycleScope.launch {
            commandContext.invokeSuspendingLifecycleCommand {
                emit(CommandState.Update("coucou suspend"))
                command<String> { "coucou" }
            }
        }
        val commandRootContext = getKoin().get<ICommandLogger<Any>>()
        mutableStateFlow.value = commandRootContext
    }

    override fun onResume() {
        super.onResume()
        commandContext.invokeLifecycleCommand<Unit> {
            emit(CommandState.Update("coucou"))
            command<String> { "hola" }
        }
    }
}