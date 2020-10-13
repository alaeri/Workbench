package com.alaeri.cats.app.command

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.alaeri.command.core.Command
import com.alaeri.command.core.command
import com.alaeri.command.di.commandModule
import org.koin.core.module.Module


/**
 * Created by Emmanuel Requier on 22/04/2020.
 */
val commandListFragmentModule = CommandListFragmentModule().module

class CommandListFragmentModule {
    val module: Command<Module> = commandModule {
        commandScope<CommandListFragment> {
            scoped<Fragment> { (fragment: CommandListFragment) -> fragment }
            factory<Lifecycle> { get<Fragment>().lifecycle }
            scoped<ViewModelStoreOwner> {
                val vmStore = ViewModelStore()
                ViewModelStoreOwner { vmStore }
            }
            factory<CommandAdapter> { CommandAdapter() }
            viewmodel<CommandListViewModel> {
                Log.d("COMMAND2_VM","CLVM")
                val commandRepository : CommandRepository = get()
                Log.d("COMMAND2_VM","commandRepository: $commandRepository")
                CommandListViewModel(commandRepository)
            }

        }
    }
}
