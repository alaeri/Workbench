package com.alaeri.cats.app.cats

import com.alaeri.cats.app.cats.api.CatApi
import com.alaeri.cats.app.cats.api.toDomain
import com.alaeri.cats.app.user.User
import java.lang.RuntimeException

/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
class CatRemoteDataSource(private val catApi: CatApi){

    suspend fun listCats(user: User, page: Int = 0) : List<Cat> {
        val limit = if(page == 0){ 30 } else { 20 }
        val catListResponse = catApi.listCats(apiKey = user.apiKey, limit = limit, page = page, order = "ASC")
        return catListResponse.body()?.map { it.toDomain() } ?: throw RuntimeException(catListResponse.errorBody()?.string())
    }

}