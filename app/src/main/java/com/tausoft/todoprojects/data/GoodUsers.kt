package com.tausoft.todoprojects.data

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "good_users", primaryKeys = ["gu_good_id", "gu_user_id"])
data class GoodUsers (
    // Права доступа пользователей к товарам
    @ColumnInfo(name = "gu_good_id")
    var gu_goodId: String = "",
    @ColumnInfo(name = "gu_user_id")
    var gu_userId: String = "",
    var gu_createdAt: Long = 0,
    var gu_modifiedAt: Long = 0,
    var gu_isDeleted: Boolean = false
) {
    init {
        val time = System.currentTimeMillis()
        gu_createdAt = time
        gu_modifiedAt = time
    }
}