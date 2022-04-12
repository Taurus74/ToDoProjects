package com.tausoft.todoprojects.data

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "good_shops", primaryKeys = ["gs_good_id", "gs_shop_id"])
data class GoodShops (
    // Права доступа пользователей к товарам
    @ColumnInfo(name = "gs_good_id")
    var gs_goodId: String = "",
    @ColumnInfo(name = "gs_shop_id")
    var gs_shopId: String = "",
    var gs_createdAt: Long = 0,
    var gs_modifiedAt: Long = 0,
    var gs_isDeleted: Boolean = false
) {
    init {
        val time = System.currentTimeMillis()
        gs_createdAt = time
        gs_modifiedAt = time
    }
}