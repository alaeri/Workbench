package com.alaeri.cats.app.command.lifecycle

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.alaeri.cats.app.command.CommandRepository
import com.alaeri.command.CommandState
import com.alaeri.command.android.CommandLogger
import com.alaeri.command.android.DelayedLogLifecycleCommandOwner
import com.alaeri.command.core.IInvokationContext
import com.alaeri.command.core.command
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent

class FragmentExample: Fragment(), DelayedLogLifecycleCommandOwner, KoinComponent {

    private val mutableStateFlow= MutableStateFlow<CommandLogger<Any>?>(null)
    override val commandContext = buildLifecycleCommandContext()
    override val futureLogger: Flow<CommandLogger<Any>?>
        get() = mutableStateFlow


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invokeLifecycleCommand {
            emit(CommandState.Update("coucou"))
        }
        lifecycleScope.launch {
            invokeSuspendingLifecycleCommand {
                emit(CommandState.Update("coucou suspend"))
                command<String> { "coucou" }
            }
        }
        val commandRootContext = getKoin().get<CommandLogger<Any>>()
        mutableStateFlow.value = commandRootContext
    }

    override fun onResume() {
        super.onResume()
        invokeLifecycleCommand {
            emit(CommandState.Update("coucou"))
            command<String> { "hola" }
        }
    }
}