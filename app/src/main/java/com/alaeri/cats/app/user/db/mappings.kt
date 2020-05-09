package com.alaeri.cats.app.user.db

import com.alaeri.cats.app.user.User

fun DBUser.toUser() = User(
    firstName = firstName,
    apiKey = apiKey,
    favoritesCount = favoritesCount
)

fun User.toDBUser() = DBUser(
    firstName = firstName,
    apiKey = apiKey,
    favoritesCount = favoritesCount
)