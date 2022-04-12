package com.tausoft.todoprojects.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tausoft.todoprojects.data.GoodImage

@Dao
interface GoodImagesDao: BaseDao<GoodImage> {
    @Query("SELECT * FROM images")
    fun getAll(): LiveData<List<GoodImage>>

    @Query("SELECT * FROM images WHERE im_id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<GoodImage>
}