package com.alaeri.cats.app.cats

import androidx.paging.DataSource
import com.alaeri.cats.app.cats.db.CatDao
import com.alaeri.cats.app.cats.db.toDb
import com.alaeri.cats.app.cats.db.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
class CatLocalDataSource (private val catDao: CatDao){


    fun storeCats(cats: List<Cat>){
        catDao.insertIfNotPresent(cats.map { it.toDb() })
    }

    val paginatedCatsDataSource : DataSource.Factory<Int, Cat> = catDao.catsByIds().map { it.toDomain() }

    val catsByIdFlow: Flow<List<Cat>> = catDao.catsByIdFlow().map{ it.map { it.toDomain() }}

}