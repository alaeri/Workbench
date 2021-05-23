package com.alaeri.cats.app.user

import com.alaeri.cats.app.LogOwner
import com.alaeri.cats.app.user.db.UserDao
import com.alaeri.cats.app.user.db.toDBUser
import com.alaeri.cats.app.user.db.toUser
import com.alaeri.cats.app.log
import com.alaeri.cats.app.logBlockingFlow
import com.alaeri.cats.app.logFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take

/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
class LocalUserDataSource(private val userDao: UserDao): LogOwner {

    suspend fun store(remoteUser: User) : Unit = log("store user"){
        val dbUser = remoteUser.toDBUser()
        val user = logFlow<User?>("user") { currentUser }.take(1).first()
        if(user != null){
            userDao.update(dbUser)
        }else{
            userDao.insert(dbUser)
        }
    }

    suspend fun remove(user: User): Unit = log("remove user"){
        userDao.delete(user.toDBUser())
    }

    val currentUser: Flow<User?> = logBlockingFlow<User?>("current user"){ userDao.currentUser.map { it?.toUser() } }

}