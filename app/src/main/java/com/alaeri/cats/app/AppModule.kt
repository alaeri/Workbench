package com.alaeri.cats.app

import androidx.room.Room
import com.alaeri.cats.app.db.AppDatabase
import com.alaeri.command.di.commandModule
import com.alaeri.ui.glide.FlowImageLoader
import org.koin.android.ext.koin.androidContext
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
        commandSingle<FlowImageLoader> {
            FlowImageLoader(get())
        }
        commandSingle<Retrofit> {
            Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create())
                .baseUrl("https://api.thecatapi.com/v1/")
                .build()
        }
    }
}
