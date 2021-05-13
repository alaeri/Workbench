package com.alaeri.cats.app.cats.api

data class ApiCat(val breeds: List<ApiBreed>,
                  val height: Int,
                  val id: String,
                  val url: String,
                  val width :Int)