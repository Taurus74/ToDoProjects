package com.tausoft.todoprojects.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tausoft.todoprojects.MainApplication
import com.tausoft.todoprojects.ToDosDatabase
import com.tausoft.todoprojects.dao.UsersDao
import com.tausoft.todoprojects.data.Users
import java.util.concurrent.Executors

class UserViewModel: ViewModel() {
    private val service = Executors.newFixedThreadPool(1)

    override fun onCleared() {
        super.onCleared()
        service.shutdown()
    }

    fun add(user: Users) {
        service.submit(UserAddRunnable(
            ToDosDatabase.invoke(MainApplication.getInstance()).usersDao(),
            user))
    }

    fun setActive(userId: String) {
        val time = System.currentTimeMillis()
        ToDosDatabase.invoke(MainApplication.getInstance()).usersDao().setActive(userId, time)
    }

    fun setInactive(userId: String) {
        val time = System.currentTimeMillis()
        ToDosDatabase.invoke(MainApplication.getInstance()).usersDao().setInactive(userId, time)
    }

    fun getUser(account: String): Users {
        return ToDosDatabase.invoke(MainApplication.getInstance())
            .usersDao().getUser(account)
    }

    fun count(): LiveData<Int> {
        return ToDosDatabase.invoke(MainApplication.getInstance())
            .usersDao().count()
    }

    fun getAll(exchangeTime: Long): List<Users> {
        return ToDosDatabase.invoke(MainApplication.getInstance())
            .usersDao().getAll(exchangeTime)
    }

    class UserAddRunnable(private val dao: UsersDao, private val user: Users): Runnable {
        override fun run() {
            dao.insert(user)
        }
    }

}