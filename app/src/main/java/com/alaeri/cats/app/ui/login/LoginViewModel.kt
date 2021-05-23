package com.alaeri.cats.app.ui.login

import androidx.lifecycle.*
import com.alaeri.cats.app.LogOwner
import com.alaeri.cats.app.user.UserRepository
import com.alaeri.cats.app.log
import com.alaeri.cats.app.logBlocking
import com.alaeri.cats.app.logBlockingFlow
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch

/**
 * TODO this would be more legible with a switchMap maybe?
 *
 */
class LoginViewModel(private val userRepository: UserRepository
) : ViewModel(), LogOwner {


    private val mediatorLiveData = MediatorLiveData<LoginState>().apply {
        logBlocking("init login live data"){
            viewModelScope.launch {
                log("init login live data coroutine"){
                    val source = logBlockingFlow("currentUser") { userRepository.currentUser }.asLiveData(
                        currentCoroutineContext())
                    addSource(source) {
                        if (it == null) {
                            if (value !is LoginState.Loading) {
                                value = LoginState.LoggedOut()
                            }
                        } else {
                            value = LoginState.LoggedIn(it)
                        }
                    }
                }
            }
        }
    }
    val currentState : LiveData<LoginState> = mediatorLiveData

    fun onSubmitClicked(firstName: String) {
        logBlocking<Unit>(name = "onSubmitClicked")
//            commandNomenclature = CommandNomenclature.Application.Cats.FirstNameSubmitted)
        {
            viewModelScope.launch {
                mediatorLiveData.value = LoginState.Loading(firstName)
                try{
                    log("login"){ userRepository.login(firstName) }
                }catch (e: Exception){
                    mediatorLiveData.value = LoginState.LoggedOut(e)
                }
            }
        }
    }

    fun onLogoutClicked() {
        val oldValue = mediatorLiveData.value
        if(oldValue is LoginState.LoggedIn){
            val user = oldValue.user
            mediatorLiveData.value = LoginState.Loading(user.firstName)
            viewModelScope.launch {
                userRepository.logout(user)
                mediatorLiveData.value = LoginState.LoggedOut()
            }
        }
    }
}
