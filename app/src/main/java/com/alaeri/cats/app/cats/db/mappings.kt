package com.alaeri.cats.app.cats.db

import com.alaeri.cats.app.cats.Cat

fun DBCat.toDomain() =
    Cat(
        id = id,
        width = width,
        height = height,
        breeds = breeds,
        url = url
    )

fun Cat.toDb() =
    DBCat(
        id = id,
        width = width,
        height = height,
        breeds = breeds,
        url = url
    )