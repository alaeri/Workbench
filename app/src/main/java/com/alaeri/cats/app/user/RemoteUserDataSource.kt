package com.alaeri.cats.app.user

import com.alaeri.cats.app.user.net.UserApi
import com.alaeri.cats.app.log

class RemoteUserDataSource(private val userApi: UserApi){

    suspend fun login(firstName: String, apiKey: String = ""): User = log("login") {
        val response = userApi.getFavorites(
            apiKey = apiKey,
            limit = 1,
            page = 1)
        val paginationCountAsString = response.headers().get("Pagination-Count")
        val favoritesCount = paginationCountAsString?.toInt() ?: 0 //TODO throw an exception
        User(
            firstName = firstName,
            apiKey = apiKey,
            favoritesCount = favoritesCount)
    }

    suspend fun fetch(localUser: User): User = log("fetch", localUser){ login(localUser.firstName, localUser.apiKey) }

}