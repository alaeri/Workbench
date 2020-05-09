package com.alaeri.cats.app.cats

/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
data class Cat(
    val id: String,
    val width: Int,
    val height: Int,
    val breeds: List<String>,
    val url: String
)