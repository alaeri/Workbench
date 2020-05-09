package com.alaeri.cats.app.ui.login

import androidx.lifecycle.*
import com.alaeri.cats.app.user.UserRepository
import com.alaeri.command.core.IInvokationContext
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.command.core.suspendInvokeAndFold
import com.alaeri.command.invokeSyncCommand
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository, private val invokationContext: IInvokationContext<*, *>) : ViewModel() {

    private val mediatorLiveData = MediatorLiveData<LoginState>()

    val currentState : LiveData<LoginState> = mediatorLiveData

    init {
        val opContext = invokationContext as IInvokationContext<Unit, Unit>
        invokeSyncCommand(opContext){
            val source = syncInvokeFlow { userRepository.currentUser }.asLiveData()
            mediatorLiveData.addSource(source){
                if(it == null){
                    if(mediatorLiveData.value !is LoginState.Loading){
                        mediatorLiveData.value = LoginState.LoggedOut()
                    }
                }else{
                    mediatorLiveData.value = LoginState.LoggedIn(it)
                }
            }
        }

    }

    fun onSubmitClicked(firstName: String) {
        val opContext = invokationContext as IInvokationContext<Unit, Unit>
        invokeSyncCommand(opContext){
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
