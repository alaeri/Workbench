package com.alaeri.cats.app

import androidx.room.Room
import com.alaeri.cats.app.db.AppDatabase
import com.alaeri.command.koin.commandModule
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val appModule = AppModule().appModule
class AppModule {
    val appModule = commandModule {
        commandSingle<AppDatabase> {
            Room.databaseBuilder(
                androidContext(),
                AppDatabase::class.java,
                "database-name"
            ).fallbackToDestructiveMigration().build()
        }
        commandSingle<com.alaeri.command.glide.FlowImageLoader> {
            com.alaeri.command.glide.FlowImageLoader(get())
        }
        commandSingle<Retrofit> {
            Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create())
                .baseUrl("https://api.thecatapi.com/v1/")
                .build()
        }
    }
}
