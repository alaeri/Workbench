package com.alaeri.cats.app.user

import com.alaeri.cats.app.user.db.UserDao
import com.alaeri.cats.app.user.db.toDBUser
import com.alaeri.cats.app.user.db.toUser
import com.alaeri.command.core.flow.FlowCommand
import com.alaeri.command.core.flow.flowCommand
import com.alaeri.command.core.flow.syncInvokeFlow
import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspend.suspendingCommand
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take

/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
class LocalUserDataSource(private val userDao: UserDao) {

    suspend fun store(remoteUser: User) : SuspendingCommand<Unit> = suspendingCommand{
        val dbUser = remoteUser.toDBUser()
        val user = syncInvokeFlow { currentUser }.take(1).first()
        if(user != null){
            userDao.update(dbUser)
        }else{
            userDao.insert(dbUser)
        }
    }

    suspend fun remove(user: User): SuspendingCommand<Unit> = suspendingCommand{
        userDao.delete(user.toDBUser())
    }

    val currentUser: FlowCommand<User?> = flowCommand{ userDao.currentUser.map { it?.toUser() } }

}