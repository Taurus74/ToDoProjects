package com.tausoft.todoprojects.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tausoft.todoprojects.MainApplication
import com.tausoft.todoprojects.ToDosDatabase
import com.tausoft.todoprojects.dao.TasksDao
import com.tausoft.todoprojects.data.Task
import java.util.concurrent.Executors

class TasksViewModel: ViewModel() {
    private val service = Executors.newFixedThreadPool(1)

    override fun onCleared() {
        super.onCleared()
        service.shutdown()
    }

    fun getAllTasks(): LiveData<List<Task>> {
        return ToDosDatabase.invoke(MainApplication.getInstance()).tasksDao().getAll()
    }

    fun getAllParents(): LiveData<List<Task>> {
        return ToDosDatabase.invoke(MainApplication.getInstance()).tasksDao().getAllParents()
    }

    fun getTask(ids: Array<String?>): LiveData<List<Task>> {
        return ToDosDatabase.invoke(MainApplication.getInstance())
            .tasksDao().loadAllByIds(ids)
    }

    fun insert(task: Task, userId: String) {
        service.submit(TaskAddRunnable(
            ToDosDatabase.invoke(MainApplication.getInstance()).tasksDao(),
            task,
            userId))
    }

    fun update(task: Task) {
        service.submit(TaskUpdateRunnable(
            ToDosDatabase.invoke(MainApplication.getInstance()).tasksDao(),
            task))
    }

    fun updateMark(taskId: String, value: Boolean) {
        service.submit(TaskUpdateMarkRunnable(
            ToDosDatabase.invoke(MainApplication.getInstance()).tasksDao(),
            taskId,
            value))
    }

    fun updateGrade(taskId: String, value: Int) {
        service.submit(TaskUpdateGradeRunnable(
            ToDosDatabase.invoke(MainApplication.getInstance()).tasksDao(),
            taskId,
            value))
    }

    fun delete(taskId: String, userId: String) {
        service.submit(TaskDelRunnable(
            ToDosDatabase.invoke(MainApplication.getInstance()).tasksDao(),
            taskId,
            userId))
    }

    fun undelete(taskId: String, userId: String) {
        service.submit(TaskUndeleteRunnable(
            ToDosDatabase.invoke(MainApplication.getInstance()).tasksDao(),
            taskId,
            userId))
    }

    fun setExpanded(taskId: String, expanded: Boolean) {
        service.submit(FolderSetExpanded(
            ToDosDatabase.invoke(MainApplication.getInstance()).tasksDao(),
            taskId,
            expanded
        ))
    }

    class TaskAddRunnable(private val dao: TasksDao,
                          private val task: Task,
                          private val userId: String): Runnable {
        override fun run() {
            dao.insert(task, userId)
        }
    }

    class TaskUpdateRunnable(private val dao: TasksDao, private val task: Task): Runnable {
        override fun run() {
            dao.update(task)
        }
    }

    class TaskUpdateMarkRunnable(
        private val dao: TasksDao,
        private val taskId: String,
        private val value: Boolean): Runnable {
        override fun run() {
            val time = System.currentTimeMillis()
            dao.updateMark(taskId, value, time)
        }

    }

    class TaskUpdateGradeRunnable(
        private val dao: TasksDao,
        private val taskId: String,
        private val value: Int): Runnable {
        override fun run() {
            val time = System.currentTimeMillis()
            dao.updateGrade(taskId, value, time)
        }
    }

    class TaskDelRunnable(
        private val dao: TasksDao,
        private val taskId: String,
        private val userId: String): Runnable {
        override fun run() {
            dao.delete(taskId, userId)
        }
    }

    class TaskUndeleteRunnable(
        private val dao: TasksDao,
        private val taskId: String,
        private val userId: String): Runnable {
        override fun run() {
            dao.undelete(taskId, userId)
        }
    }

    class FolderSetExpanded(
        private val dao: TasksDao,
        private val taskId: String,
        private val expanded: Boolean): Runnable {
        override fun run() {
            dao.setExpanded(taskId, expanded)
            Log.d(MainApplication.TAG, "taskId = $taskId, set expanded = $expanded")
        }
    }

    fun count(): LiveData<Int> {
        return ToDosDatabase.invoke(MainApplication.getInstance())
            .tasksDao().count()
    }

    fun getAll(exchangeTime: Long): List<Task> {
        return ToDosDatabase.invoke(MainApplication.getInstance())
            .tasksDao().getAll(exchangeTime)
    }

}