package com.alaeri.cats.app.cats.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CatApi{

    @GET("https://api.thecatapi.com/v1/images/search")
    suspend fun listCats(@Header("x-api-key") apiKey: String,
                 @Query("size") size: String? = "SMALL",
                 @Query("order") order: String? = null,
                 @Query("mime-types") mimeTypes: List<String>? = null,
                 @Query("limit") limit: Int = 100,
                 @Query("page") page: Int = 0,
                 @Query("category_ids") categoryIds: List<Int>? = null,
                 @Query("format") format: String? = null,
                 @Query("breed_id") breedId: String? = null) : Response<List<ApiCat>>

}