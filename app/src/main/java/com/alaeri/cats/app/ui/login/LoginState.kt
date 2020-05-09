package com.alaeri.cats.app.ui.login

import com.alaeri.cats.app.user.User
import java.lang.Exception

/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
sealed class LoginState{
    data class LoggedOut(val loginError: Exception? = null): LoginState()
    data class Loading(val firstName: String): LoginState()
    data class LoggedIn(val user: User): LoginState()
}