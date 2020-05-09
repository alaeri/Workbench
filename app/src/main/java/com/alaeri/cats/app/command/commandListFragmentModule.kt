package com.alaeri.cats.app.command

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.alaeri.command.core.Command
import com.alaeri.command.core.invoke
import com.alaeri.command.core.command
import com.alaeri.command.core.invokeCommand
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Created by Emmanuel Requier on 22/04/2020.
 */
val commandListFragmentModule = CommandListFragmentModule().module

class CommandListFragmentModule {
    val module: Command<Module> = command {
        module {

            scope<CommandListFragment> {
                scoped { (fragment: Fragment) -> invoke { command<Fragment> { fragment } } }
                factory { invoke { command<Lifecycle> { get<Fragment>().lifecycle } } }
                scoped<ViewModelStoreOwner> {
                    invoke {
                        command<ViewModelStoreOwner> {
                            val vmStore = ViewModelStore()
                            ViewModelStoreOwner { vmStore }
                        }
                    }
                }
                factory {
                    this@command.invokeCommand<Module,CommandAdapter> {
                        CommandAdapter()
                    }
                }
                viewModel<CommandListViewModel> {
                    invokeCommand {
                        CommandListViewModel(
                            invokeCommand<Module,CommandRepository> {
                                get()
                            })
                    }
                }
            }
        }
    }
}
