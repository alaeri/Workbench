package com.alaeri.cats.app.user

import com.alaeri.cats.app.LogOwner
import com.alaeri.cats.app.log
import com.alaeri.cats.app.logBlockingFlow
import com.alaeri.cats.app.logFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
class UserRepository(private val remoteUserDataSource: RemoteUserDataSource,
                     private val localUserDataSource: LocalUserDataSource,
                     private val defaultContext: CoroutineContext) : LogOwner{

    suspend fun login(firstName: String) : Unit = log("login"){
        withContext(defaultContext){
            val remoteUser = log("fetchRemote"){
                remoteUserDataSource.login(firstName)
            }
            log("storeRemoteInLocal", remoteUser){
                localUserDataSource.store(remoteUser)
            }
        }
    }

    suspend fun refresh(): Unit = log("refresh") {
        withContext(defaultContext){
//            emit(Step("loadingUserFromDb"))
            val localUser = logFlow("current user flow"){ localUserDataSource.currentUser }.take(1).first()
//            emit(Step("fetchingUserFrom"))
            val remoteUser = log("fetch remote user"){ remoteUserDataSource.fetch(localUser!!) }
//            emit(Step("storingUserInDb"))
            localUserDataSource.store(remoteUser)
        }
    }

    suspend fun logout(user: User) : Unit = log("logout"){
        withContext(defaultContext){
            log("remove user"){ localUserDataSource.remove(user) }
        }
    }

    val currentUser: Flow<User?>
        get() = logBlockingFlow("currentUser") {
        localUserDataSource.currentUser
    }

}