package com.tausoft.todoprojects.data

import androidx.room.Entity
import java.util.*

@Entity(tableName = "shops", primaryKeys = ["sh_id"])
data class Shops (
    // Магазины
    var sh_id: String = UUID.randomUUID().toString(),
    var sh_name // название
            : String = "",
    var sh_note // примечание
            : String = "",
    var sh_createdAt: Long = 0,
    var sh_modifiedAt: Long = 0,
    var sh_isDeleted: Boolean = false

) {
    init {
        val time = System.currentTimeMillis()
        sh_createdAt = time
        sh_modifiedAt = time
    }
}