package com.alaeri.cats.app.user.net

import com.squareup.moshi.Json

/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
data class ApiFavorite(
    val createdAt: String,
    val id: Int,
    @Json(name="image_id")
    val imageId: String,
    @Json(name="sub_id")
    val subscriptionId: String,
    @Json(name="user_id")
    val userId: String
)