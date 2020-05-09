package com.alaeri.cats.app.user

import android.util.Log
import com.alaeri.cats.app.db.AppDatabase
import com.alaeri.cats.app.ui.login.LoginViewModel
import com.alaeri.cats.app.user.net.UserApi
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
val userModule = UserModule().module
class UserModule{
    val module = command<Module> {
        module {
            single { invoke{ command<UserApi>{ get<Retrofit>().create(UserApi::class.java) } }}
            single { invoke{ command<RemoteUserDataSource> { RemoteUserDataSource(get()) } } }
            single { invoke{ command<LocalUserDataSource> { LocalUserDataSource(get<AppDatabase>().userDao()) } } }
            single<UserRepository> {
                Log.d("CATS","test")
                val userRepository = invoke{ command<UserRepository> { UserRepository(get(), get(), Dispatchers.IO) } }
                Log.d("CATS","test2")
                userRepository
            }
            viewModel { invoke{ command<LoginViewModel> {  LoginViewModel(get(), get()) } } }
        }
    }
}

fun processE(e: Throwable){
    Log.e("OPERATION", "error",e)
    e.cause?.let { processE(e) }
}