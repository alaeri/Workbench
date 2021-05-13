package com.alaeri.cats.app.cats.api

import com.squareup.moshi.Json

/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
data class ApiBreed(
    val id: String,
    val name: String,
    @Json(name = "alt_names")
    val altNames: String,
    val wikipediaUrl: String
)