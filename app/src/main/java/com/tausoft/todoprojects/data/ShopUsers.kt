package com.tausoft.todoprojects.data

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "shop_users", primaryKeys = ["su_shop_id", "su_user_id"])
data class ShopUsers (
    // Права доступа пользователей к магазинам
    @ColumnInfo(name = "su_shop_id")
    var su_shopId: String = "",
    @ColumnInfo(name = "su_user_id")
    var su_userId: String = "",
    var su_createdAt: Long = 0,
    var su_modifiedAt: Long = 0,
    var su_isDeleted: Boolean = false
) {
    init {
        val time = System.currentTimeMillis()
        su_createdAt = time
        su_modifiedAt = time
    }
}