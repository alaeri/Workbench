package com.alaeri.cats.app.ui.cats

import com.alaeri.cats.app.cats.CatRepository
import com.alaeri.cats.app.user.UserRepository
import com.alaeri.command.CommandState
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.suspendingCommand
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take

class RefreshUseCase(private val userRepository: UserRepository, private val catRepository: CatRepository){

    suspend operator fun invoke(): SuspendingCommand<NetworkState> = suspendingCommand {
        emit(CommandState.Update(NetworkState.Loading))
        try {
            val user = syncInvokeFlow{ userRepository.currentUser } .take(1).first()
            if (user != null) {
                catRepository.refresh(user)
            } else {
                throw RuntimeException("no user connected")
            }
            NetworkState.Idle()
        } catch (e: Exception) {
            NetworkState.Idle(e)
        }
    }
}