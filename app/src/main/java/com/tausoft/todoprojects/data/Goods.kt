package com.tausoft.todoprojects.data

import androidx.room.Entity
import java.util.*

@Entity(tableName = "goods", primaryKeys = ["gd_id"])
data class Goods (
    // Товары
    var gd_id: String = UUID.randomUUID().toString(),
    var gd_name // название
            : String,
    var gd_note // примечание
            : String = "",
    var gd_createdAt: Long = 0,
    var gd_modifiedAt: Long = 0,
    var gd_isDeleted: Boolean = false
) {
    init {
        val time = System.currentTimeMillis()
        gd_createdAt = time
        gd_modifiedAt = time
    }
}