package com.alaeri.cats.app.user.db

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
@Entity(tableName = "user2")
data class DBUser(
    @PrimaryKey @ColumnInfo(name = "first_name") var firstName: String,
    @ColumnInfo(name = "api_key") var apiKey: String,
    @ColumnInfo(name = "favorites_count") var favoritesCount: Int)