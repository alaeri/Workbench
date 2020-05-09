package com.alaeri.cats.app.cats.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
@Entity(tableName = "cat")
data class DBCat(
    @PrimaryKey val id: String,
    val width: Int, val height: Int,
    @TypeConverters(value = [BreedsConverter::class])
    val breeds: List<String>,
    val url: String)