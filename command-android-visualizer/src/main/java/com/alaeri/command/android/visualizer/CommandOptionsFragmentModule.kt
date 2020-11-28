package com.alaeri.command.android.visualizer

import com.alaeri.command.android.visualizer.option.FilterCommandRepository
import com.alaeri.command.android.visualizer.option.VisualizationOptionsFragmentViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Created by Emmanuel Requier on 28/11/2020.
 */
class CommandOptionsFragmentModule {
    val module : Module = module {
        single<FilterCommandRepository>{
            FilterCommandRepository(get())
        }
        viewModel {
            VisualizationOptionsFragmentViewModel(get())
        }

    }

}