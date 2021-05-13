package com.alaeri.cats.app

import androidx.room.Room
import com.alaeri.cats.app.db.AppDatabase
import com.alaeri.log.glide.FlowImageLoader
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val appModule = AppModule().appModule
class AppModule {
    val appModule = module {
        single<AppDatabase> {
            Room.databaseBuilder(
                androidContext(),
                AppDatabase::class.java,
                "database-name"
            ).fallbackToDestructiveMigration().build()
        }
        single {
            FlowImageLoader(get())
        }
        single<Retrofit> {
            Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create())
                .baseUrl("https://api.thecatapi.com/v1/")
                .build()
        }
    }
}
