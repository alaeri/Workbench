package com.alaeri.cats.app.user

import android.util.Log
import com.alaeri.cats.app.db.AppDatabase
import com.alaeri.cats.app.ui.login.LoginViewModel
import com.alaeri.cats.app.user.net.UserApi
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

    val module : Module = module {
        single<UserApi> {  get <Retrofit>().create(UserApi::class.java) }
        single<RemoteUserDataSource> {  RemoteUserDataSource(get()) }
        single<LocalUserDataSource> { LocalUserDataSource(get<AppDatabase>().userDao()) }
        single<UserRepository> {
            Log.d("CATS","test")
            val userRepository =  UserRepository(get(), get(), Dispatchers.IO)
            Log.d("CATS","test2")
            userRepository
        }
        viewModel<LoginViewModel> {
            val userRepository : UserRepository = get()
            Log.d("COMMAND3", "$userRepository")
            LoginViewModel(userRepository)
        }
    }
}