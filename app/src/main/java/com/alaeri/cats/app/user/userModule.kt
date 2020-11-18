package com.alaeri.cats.app.user

import android.util.Log
import com.alaeri.command.DefaultIRootCommandLogger
import com.alaeri.cats.app.db.AppDatabase
import com.alaeri.cats.app.ui.login.LoginViewModel
import com.alaeri.cats.app.user.net.UserApi
import com.alaeri.command.core.Command
import com.alaeri.command.koin.commandModule
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import retrofit2.Retrofit

/**
 * Created by Emmanuel Requier on 19/04/2020.
 */
val userModule = UserModule().module
class UserModule{

    val module : Command<Module> = commandModule {
        commandSingle<UserApi> {  get<Retrofit>().create(UserApi::class.java) }
        commandSingle<RemoteUserDataSource> {  RemoteUserDataSource(get()) }
        commandSingle<LocalUserDataSource> { LocalUserDataSource(get<AppDatabase>().userDao()) }
        commandSingle<UserRepository> {
            Log.d("CATS","test")
            val userRepository =  UserRepository(get(), get(), Dispatchers.IO)
            Log.d("CATS","test2")
            userRepository
        }
        commandViewModel<LoginViewModel> {
            val userRepository : UserRepository = get()
            Log.d("COMMAND3", "$userRepository")
            val invokationContext : DefaultIRootCommandLogger = get()
            Log.d("COMMAND3", "$invokationContext")
            LoginViewModel(userRepository, get())
        }
    }
}