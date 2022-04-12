package com.tausoft.todoprojects.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tausoft.todoprojects.data.Shops

@Dao
interface ShopsDao: BaseDao<Shops> {
    @Query("SELECT * FROM shops")
    fun getAll(): LiveData<List<Shops>>

    @Query("SELECT * FROM shops WHERE sh_id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<Shops>
}