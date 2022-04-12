package com.tausoft.todoprojects.dao

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.tausoft.todoprojects.MainApplication
import com.tausoft.todoprojects.ToDosDatabase
import com.tausoft.todoprojects.data.TaskUsers
import com.tausoft.todoprojects.data.Task

@Dao
interface TasksDao: BaseDao<Task> {
    companion object {
        private const val TAG = "TasksDao"
    }

    // Добавить задачу И ключ доступа для пользователя-создателя
    @Transaction
    fun insert(task: Task, userId: String) {
        insert(task)
        Log.d(TAG, "task inserted")

        val taskUser = TaskUsers(task.ts_id, userId, task.ts_createdAt, task.ts_createdAt)
        ToDosDatabase.invoke(MainApplication.getInstance())
            .taskUserDao().insert(taskUser)
        Log.d(TAG, "task_user inserted")
    }

    @Query("UPDATE tasks SET ts_mark = (:mark), ts_modifiedAt = (:time) WHERE ts_id = (:taskId)")
    fun updateMark(taskId: String, mark: Boolean, time: Long)

    @Query("UPDATE tasks SET ts_grade = (:grade), ts_modifiedAt = (:time) WHERE ts_id = (:taskId)")
    fun updateGrade(taskId: String, grade: Int, time: Long)

    // Пометить на удаление задачу И ключ доступа для пользователя-создателя
    @Transaction
    fun delete(taskId: String, userId: String) {
        val time = System.currentTimeMillis()
        delete(taskId, time)
        Log.d(TAG, "task deleted")

        ToDosDatabase.invoke(MainApplication.getInstance())
            .taskUserDao().delete(taskId, userId, time)
        Log.d(TAG, "task_user deleted")
    }

    @Query("UPDATE tasks SET ts_isDeleted = 1, ts_modifiedAt = (:time) WHERE ts_id = (:taskId)")
    fun delete(taskId: String, time: Long)

    // Снять пометку удаления задачи И ключа доступа для пользователя-создателя
    @Transaction
    fun undelete(taskId: String, userId: String) {
        val time = System.currentTimeMillis()
        undelete(taskId, time)
        Log.d(TAG, "task undeleted")

        ToDosDatabase.invoke(MainApplication.getInstance())
            .taskUserDao().undelete(taskId, userId, time)
        Log.d(TAG, "task_user undeleted")
    }

    @Query("UPDATE tasks SET ts_isDeleted = 0, ts_modifiedAt = (:time) WHERE ts_id = (:taskId)")
    fun undelete(taskId: String, time: Long)

    // TODO - выборка элементов, доступных userId
    @Query("SELECT * FROM tasks WHERE ts_isDeleted = 0 ORDER BY ts_type DESC, ts_name")
    fun getAll(): LiveData<List<Task>>

    // TODO - выборка элементов, доступных userId
    @Query("SELECT * FROM tasks WHERE ts_isDeleted = 0 AND ts_type <> 0 ORDER BY ts_type DESC, ts_name")
    fun getAllParents(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE ts_id IN (:ids) AND ts_isDeleted = 0")
    fun loadAllByIds(ids: Array<String?>): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE ts_id = (:id)")
    fun get(id: String): LiveData<Task>

    @Query("SELECT COUNT(ts_id) FROM tasks")
    fun count(): LiveData<Int>

    @Query("UPDATE tasks SET ts_expanded = (:expanded) WHERE ts_id = (:taskId)")
    fun setExpanded(taskId: String, expanded: Boolean)

    // TODO - выборка элементов, доступных userId
    @Query("SELECT * FROM tasks WHERE ts_modifiedAt >= (:time)")
    fun getAll(time: Long): List<Task>
}