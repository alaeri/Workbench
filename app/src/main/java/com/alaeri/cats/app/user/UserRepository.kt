package com.alaeri.cats.app.user

import com.alaeri.command.Step
import com.alaeri.command.Waiting
import com.alaeri.command.core.flow.FlowCommand
import com.alaeri.command.core.flow.IFlowCommand
import com.alaeri.command.core.flow.flowCommand
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.suspendingCommand
import com.alaeri.command.core.suspendInvokeAndFold
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
class UserRepository(private val remoteUserDataSource: RemoteUserDataSource,
                     private val localUserDataSource: LocalUserDataSource,
                     private val defaultContext: CoroutineContext){

    suspend fun login(firstName: String) : SuspendingCommand<Unit> = suspendingCommand{
        emit(Waiting())
        withContext(defaultContext){
            emit(Step("fetchingUser"))
            val remoteUser = suspendInvokeAndFold{ remoteUserDataSource.login(firstName) }
            emit(Step("storingUser"))
            suspendInvokeAndFold{ localUserDataSource.store(remoteUser) }
        }
    }

    suspend fun refresh(): SuspendingCommand<Unit> = suspendingCommand {
        withContext(defaultContext){
            emit(Step("loadingUserFromDb"))
            val localUser = syncInvokeFlow{ localUserDataSource.currentUser }.take(1).first()
            emit(Step("fetchingUserFrom"))
            val remoteUser = suspendInvokeAndFold{ remoteUserDataSource.fetch(localUser!!) }
            emit(Step("storingUserInDb"))
            localUserDataSource.store(remoteUser)
        }
    }

    suspend fun logout(user: User) : SuspendingCommand<Unit> = suspendingCommand{
        withContext(defaultContext){
            suspendInvokeAndFold{ localUserDataSource.remove(user) }
        }
    }

    val currentUser: IFlowCommand<User?> = flowCommand {
       syncInvokeFlow { localUserDataSource.currentUser }
    }

}