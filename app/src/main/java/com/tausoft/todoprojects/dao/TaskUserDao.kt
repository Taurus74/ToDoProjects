package com.tausoft.todoprojects.dao

import androidx.room.Dao
import androidx.room.Query
import com.tausoft.todoprojects.data.TaskUsers

@Dao
interface TaskUserDao: BaseDao<TaskUsers> {
    // Пометить на удаление ключ доступа для пользователя-создателя
    @Query("""
        UPDATE task_users 
        SET tu_isDeleted = 1, tu_modifiedAt = (:time)
        WHERE tu_taskId = (:taskId) AND tu_userId = (:userId)""")
    fun delete(taskId: String, userId: String, time: Long)

    // Снять пометку удаления ключа доступа для пользователя-создателя
    @Query("""
        UPDATE task_users 
        SET tu_isDeleted = 0, tu_modifiedAt = (:time)
        WHERE tu_taskId = (:taskId) AND tu_userId = (:userId)""")
    fun undelete(taskId: String, userId: String, time: Long)

    @Query("SELECT * FROM task_users WHERE tu_modifiedAt >= (:time)")
    fun getAll(time: Long): List<TaskUsers>

    @Query("SELECT * FROM task_users WHERE tu_taskId = (:task_id) AND tu_userId = (:user_id)")
    fun get(task_id: String, user_id: String): TaskUsers
}