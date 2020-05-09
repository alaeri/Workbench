package com.alaeri.cats.app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alaeri.cats.app.cats.db.BreedsConverter
import com.alaeri.cats.app.cats.db.CatDao
import com.alaeri.cats.app.cats.db.DBCat
import com.alaeri.cats.app.user.db.DBUser
import com.alaeri.cats.app.user.db.UserDao

/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
@Database(entities = [DBUser::class, DBCat::class], version = 2, exportSchema = false)
@TypeConverters(BreedsConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun catDao(): CatDao
}
