package com.alaeri.cats.app.cats

import com.alaeri.cats.app.cats.api.CatApi
import com.alaeri.cats.app.db.AppDatabase
import com.alaeri.cats.app.ui.cats.CatsViewModel
import com.alaeri.command.core.invoke
import com.alaeri.command.core.command
import kotlinx.coroutines.Dispatchers
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit

/**
 * Created by Emmanuel Requier on 19/04/2020.
 */
val catsModule = Any().command<Module> {
    module {

        single { invoke { command<CatApi> { get<Retrofit>().create(CatApi::class.java) } } }
        single { invoke { command<CatRemoteDataSource> { CatRemoteDataSource(get()) } } }
        single { invoke { command<CatLocalDataSource> { CatLocalDataSource(get<AppDatabase>().catDao()) } } }
        single { invoke { command<CatRepository> { CatRepository(get(), get(), Dispatchers.IO) } } }
        viewModel { invoke { command<CatsViewModel> { CatsViewModel(get(), get(), get()) } } }

    }
}