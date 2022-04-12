package com.tausoft.todoprojects.data

import androidx.room.Entity

@Entity(tableName = "users", primaryKeys = ["us_account"])
data class Users (
    // Пользователи
    var us_account // google account
            : String = "",
    var us_owner: Boolean // владелец (true -> только один на всю таблицу)
            = false,
    var us_createdAt: Long = 0,
    var us_modifiedAt: Long = 0,
    var us_isDeleted: Boolean = false
) {
    init {
        val time = System.currentTimeMillis()
        us_createdAt = time
        us_modifiedAt = time
    }
}