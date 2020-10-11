package com.alaeri.cats.app.cats

import com.alaeri.cats.app.cats.api.CatApi
import com.alaeri.cats.app.db.AppDatabase
import com.alaeri.cats.app.ui.cats.CatsViewModel
import com.alaeri.command.di.commandModule
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit

/**
 * Created by Emmanuel Requier on 19/04/2020.
 */
val catsModule = Any().commandModule {

    commandSingle<CatApi> { get<Retrofit>().create(CatApi::class.java) }
    commandSingle<CatRemoteDataSource> { CatRemoteDataSource(get()) }
    commandSingle<CatLocalDataSource> { CatLocalDataSource(get<AppDatabase>().catDao()) }
    commandSingle<CatRepository> { CatRepository(get(), get(), Dispatchers.IO) }
    commandViewModel<CatsViewModel> { CatsViewModel(get(), get(), get()) }
}