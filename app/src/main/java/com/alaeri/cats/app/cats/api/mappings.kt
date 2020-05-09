package com.alaeri.cats.app.cats.api

import com.alaeri.cats.app.cats.Cat

fun ApiCat.toDomain() =
    Cat(
        id = id,
        url = url,
        width = width,
        height = height,
        breeds = breeds.map { it.name }
    )