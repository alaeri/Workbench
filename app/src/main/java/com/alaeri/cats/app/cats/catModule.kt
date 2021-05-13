package com.alaeri.cats.app.cats

import com.alaeri.cats.app.cats.api.CatApi
import com.alaeri.cats.app.db.AppDatabase
import com.alaeri.cats.app.ui.cats.CatsViewModel
//import com.alaeri.log.koin.module
//import com.alaeri.log.koin.viewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

/**
 * Created by Emmanuel Requier on 19/04/2020.
 */
object CatsModelModule
val catsModule = module {

    single<CatApi> { get<Retrofit>().create(CatApi::class.java) }
    single<CatRemoteDataSource> { CatRemoteDataSource(get()) }
    single<CatLocalDataSource> { CatLocalDataSource(get<AppDatabase>().catDao()) }
    single<CatRepository> { CatRepository(get(), get(), Dispatchers.IO) }
    viewModel<CatsViewModel> { CatsViewModel(get(), get()) }
}