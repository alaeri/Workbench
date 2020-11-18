package com.alaeri.cats.app.ui.login

import androidx.lifecycle.*
import com.alaeri.command.DefaultIRootCommandLogger
import com.alaeri.cats.app.user.UserRepository
import com.alaeri.command.ICommandRootOwner
import com.alaeri.command.CommandNomenclature
import com.alaeri.command.buildCommandRoot
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.command.core.suspendInvokeAndFold
import com.alaeri.command.invokeRootCommand
import kotlinx.coroutines.launch

/**
 * TODO this would be more legible with a switchMap maybe?
 *
 */
class LoginViewModel(private val userRepository: UserRepository,
                     private val defaultSerializer: DefaultIRootCommandLogger
) : ICommandRootOwner, ViewModel() {

    override val commandRoot = buildCommandRoot(this, null, CommandNomenclature.Root, defaultSerializer)

    private val mediatorLiveData = MediatorLiveData<LoginState>().apply {
        invokeRootCommand("init login live data", CommandNomenclature.Application.Cats.InitLoginMediator){
            val source = syncInvokeFlow { userRepository.currentUser }.asLiveData()
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
    val currentState : LiveData<LoginState> = mediatorLiveData

    fun onSubmitClicked(firstName: String) {
        invokeRootCommand<Unit>(name = "onSubmitClicked",
            commandNomenclature = CommandNomenclature.Application.Cats.FirstNameSubmitted){
            viewModelScope.launch {
                mediatorLiveData.value = LoginState.Loading(firstName)
                try{
                    suspendInvokeAndFold{ userRepository.login(firstName) }
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
