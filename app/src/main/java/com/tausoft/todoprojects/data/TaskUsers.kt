package com.tausoft.todoprojects.data

import androidx.room.Entity

@Entity(tableName = "task_users", primaryKeys = (["tu_taskId", "tu_userId"]))
data class TaskUsers (
    // Права доступа пользователей к задачам
    var tu_taskId: String = "",
    var tu_userId: String = "",
    var tu_createdAt: Long = 0,
    var tu_modifiedAt: Long = 0,
    var tu_isDeleted: Boolean = false
) {
    init {
        val time = System.currentTimeMillis()
        tu_createdAt = time
        tu_modifiedAt = time
    }
}