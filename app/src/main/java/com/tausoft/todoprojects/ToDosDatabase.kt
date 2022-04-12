package com.tausoft.todoprojects

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tausoft.todoprojects.dao.*
import com.tausoft.todoprojects.data.*

@Database(
    entities = [GoodImage::class, Goods::class, GoodTasks::class, GoodUsers::class,
        Shops::class, Task::class, TaskUsers::class, Users::class],
    version = 1
)
abstract class ToDosDatabase: RoomDatabase() {
    abstract fun goodImagesDao(): GoodImagesDao
    abstract fun shopsDao(): ShopsDao
    abstract fun tasksDao(): TasksDao
    abstract fun taskUserDao(): TaskUserDao
    abstract fun usersDao(): UsersDao

    companion object {
        private const val DBNAME = "todos.db"
        @Volatile private var instance: ToDosDatabase? = null
        private val LOCK = Object()
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance?: buildDatabase(context.applicationContext).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, ToDosDatabase::class.java, DBNAME)
                .build()
    }
}