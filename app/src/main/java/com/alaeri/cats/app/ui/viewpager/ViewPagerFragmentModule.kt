package com.alaeri.cats.app.ui.viewpager


import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.alaeri.command.core.Command
import com.alaeri.command.di.commandModule
import org.koin.core.module.Module

/**
 * Created by Emmanuel Requier on 22/04/2020.
 */
val viewPagerFragmentModule = ViewPagerFragmentModule().viewPagerFragmentModule

class ViewPagerFragmentModule{

    val viewPagerFragmentModule : Command<Module> = commandModule {
        commandScope<ViewPagerFragment> {
            scoped<Fragment> { (fragment: Fragment) -> fragment  }
//            factory<Lifecycle> { get<Fragment>().lifecycle  }
            viewmodel<ViewPagerViewModel> { ViewPagerViewModel(get()) }
        }
    }
}

