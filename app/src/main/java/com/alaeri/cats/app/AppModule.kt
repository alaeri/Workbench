package com.alaeri.cats.app

import androidx.room.Room
import com.alaeri.cats.app.db.AppDatabase
import com.alaeri.command.core.Command
import com.alaeri.command.core.command
import com.alaeri.command.core.invoke
import com.alaeri.ui.glide.FlowImageLoader
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val appModule = AppModule().appModule
class AppModule {
    val appModule : Command<Module> = command {
        module {
            single<AppDatabase> {
                invoke {
                    command<AppDatabase> {
                        Room.databaseBuilder(
                            androidContext(),
                            AppDatabase::class.java,
                            "database-name"
                        ).fallbackToDestructiveMigration().build()
                    }
                }
            }


            single<FlowImageLoader> {
                invoke {
                    command<FlowImageLoader> {
                        FlowImageLoader(androidContext())
                    }
                }
            }

            single<Retrofit> {
                invoke {
                    command<Retrofit> {
                        Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create())
                            .baseUrl("https://api.thecatapi.com/v1/")
                            .build()
                    }
                }
            }
        }
    }

}
