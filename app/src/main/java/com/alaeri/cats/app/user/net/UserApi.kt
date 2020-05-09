package com.alaeri.cats.app.user.net

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface UserApi{

    @GET("/favorites")
    suspend fun getFavorites(
        @Header(value = "x-api-key") apiKey: String,
        @Query(value = "limit") limit: Int,
        @Query(value = "page") page: Int,
        @Query(value = "sub_id") subscriptionId: String? = null): Response<List<ApiFavorite>>


}