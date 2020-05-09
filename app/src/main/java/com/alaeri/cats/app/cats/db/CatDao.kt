package com.alaeri.cats.app.cats.db

import androidx.paging.DataSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CatDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIfNotPresent(cats: List<DBCat>)


    @Query("SELECT * FROM cat ORDER BY id ASC")
    fun catsByIds(): DataSource.Factory<Int, DBCat>

    @Query("SELECT * FROM cat ORDER BY id ASC")
    fun catsByIdFlow(): Flow<List<DBCat>>

}