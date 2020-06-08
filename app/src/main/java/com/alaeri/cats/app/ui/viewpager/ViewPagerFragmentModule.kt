package com.alaeri.cats.app.ui.viewpager


import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.alaeri.command.core.Command
import com.alaeri.command.core.command
import com.alaeri.command.core.invoke
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Created by Emmanuel Requier on 22/04/2020.
 */
val viewPagerFragmentModule = ViewPagerFragmentModule().viewPagerFragmentModule

class ViewPagerFragmentModule{
    val viewPagerFragmentModule : Command<Module> = Any().command {
        module {
            scope<ViewPagerFragment> {
                scoped { (fragment: Fragment) -> invoke { command<Fragment> { fragment } } }
                factory { invoke { command<Lifecycle> { get<Fragment>().lifecycle } } }
                viewModel { invoke { command<ViewPagerViewModel> { ViewPagerViewModel() } } }
            }
        }
    }
}

