package com.alaeri.cats.app.cats

import android.util.Log
import androidx.paging.DataSource
import com.alaeri.cats.app.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * Created by Emmanuel Requier on 18/04/2020.
 */
class CatRepository(private val catLocalDataSource: CatLocalDataSource,
                    private val catRemoteDataSource: CatRemoteDataSource,
                    private val defaultCoroutineContext: CoroutineContext){

    val paginatedCatDataSource : DataSource.Factory<Int, Cat> = catLocalDataSource.paginatedCatsDataSource

    suspend fun refresh(user: User){
        withContext(defaultCoroutineContext){
            Log.d("CATS", "cats going to refresh")
            val remoteList = catRemoteDataSource.listCats(user)
            Log.d("CATS", "cats: $remoteList")
            catLocalDataSource.storeCats(remoteList)
        }
    }

    suspend fun loadMore(user: User){
        withContext(defaultCoroutineContext){
            val remoteList = catRemoteDataSource.listCats(user)
            Log.d("CATS", "cats: $remoteList")
            catLocalDataSource.storeCats(remoteList)
        }
    }

}