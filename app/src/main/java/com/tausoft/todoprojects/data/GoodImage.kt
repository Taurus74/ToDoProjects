package com.tausoft.todoprojects.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

@Entity(tableName = "images", primaryKeys = ["im_id"])
data class GoodImage (
    // Изображения товаров
    var im_id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "im_good_id")
    var im_goodId: String = "",
    var im_file: String = "",
    var im_createdAt: Long = 0,
    var im_modifiedAt: Long = 0,
    var im_isDeleted: Boolean = false

) {
    init {
        val time = System.currentTimeMillis()
        im_createdAt = time
        im_modifiedAt = time
    }
}