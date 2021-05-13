package com.alaeri.cats.app.ui.viewpager


import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Created by Emmanuel Requier on 22/04/2020.
 */
val viewPagerFragmentModule = ViewPagerFragmentModule().viewPagerFragmentModule

class ViewPagerFragmentModule{

    val viewPagerFragmentModule  = module {
        scope<ViewPagerFragment> {
            scoped<Fragment> { (fragment: Fragment) -> fragment  }
            factory<Lifecycle> { get<Fragment>().lifecycle  }
            viewModel<ViewPagerViewModel> { ViewPagerViewModel() }
        }
    }
}

