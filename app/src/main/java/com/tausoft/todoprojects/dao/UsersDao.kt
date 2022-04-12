package com.tausoft.todoprojects.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tausoft.todoprojects.data.Users

@Dao
interface UsersDao: BaseDao<Users> {
    @Query("SELECT * FROM users")
    fun getAll(): LiveData<List<Users>>

    @Query("UPDATE users SET us_owner = 1, us_modifiedAt = (:time) WHERE us_account = (:account)")
    fun setActive(account: String, time: Long)

    @Query("UPDATE users SET us_owner = 0, us_modifiedAt = (:time) WHERE us_account = (:account)")
    fun setInactive(account: String, time: Long)

    @Query("SELECT * FROM users WHERE us_account = (:account)")
    fun getUser(account: String): Users

    @Query("SELECT COUNT(us_account) FROM users")
    fun count(): LiveData<Int>

    @Query("SELECT * FROM users WHERE us_modifiedAt >= (:time)")
    fun getAll(time: Long): List<Users>
}