package com.alaeri.cats.app.user.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
abstract class UserDao {

    @Insert
    abstract fun insert(dbUser: DBUser)

    @Update
    abstract fun update(dbUser: DBUser)

    @Delete
    abstract fun delete(toDBUser: DBUser)

    @get:Query("SELECT * FROM user2")
    protected abstract val allUsers: Flow<List<DBUser>>

    /**
     * This val should not be inlined or the creation of the flow will happen
     * when the class is instantiated and this crashes the app as the userDao builder
     * does not support this.
     */
    val currentUser: Flow<DBUser?>
        get() = allUsers.map { it.firstOrNull() }

}