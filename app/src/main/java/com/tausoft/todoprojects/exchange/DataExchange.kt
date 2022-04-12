package com.tausoft.todoprojects.exchange

import android.util.Log
import com.tausoft.todoprojects.MainApplication
import com.tausoft.todoprojects.MainApplication.Companion.TAG
import com.tausoft.todoprojects.ToDosDatabase
import com.tausoft.todoprojects.data.Task
import com.tausoft.todoprojects.data.TaskUsers
import com.tausoft.todoprojects.data.Users
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableCompletableObserver
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object DataExchange {
    private const val BASE_GET_URL = "gets/"
    private const val BASE_POST_URL = "posts/"

    private var getAPI: ExchangeAPI
    private var postAPI: ExchangeAPI
    private var compositeDisposable = CompositeDisposable()

    private var tasksGot = PublishedBoolean(false)
    private var usersGot = PublishedBoolean(false)
    private var taskUsersGot = PublishedBoolean(false)

    private var tasksPosted = PublishedBoolean(false)
    private var usersPosted = PublishedBoolean(false)
    private var taskUsersPosted = PublishedBoolean(false)

    init {
        Log.d(TAG, "DataExchange init...")

        val baseGetUrl: String = MainApplication.baseUrl.toString() + BASE_GET_URL
        val basePostUrl: String = MainApplication.baseUrl.toString() + BASE_POST_URL

        val interceptor = HttpLoggingInterceptor { message -> Log.d(TAG, "OkHttp -> $message") }
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val gsonTask = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(Task::class.java, TaskDeserializer())
            .create()

        val gsonTaskUser = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(TaskUsers::class.java, TaskUserDeserializer())
            .create()

        val gsonUser = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(Users::class.java, UserDeserializer())
            .create()

        val retrofitGet = Retrofit.Builder()
            .baseUrl(baseGetUrl)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gsonTask))
            .addConverterFactory(GsonConverterFactory.create(gsonTaskUser))
            .addConverterFactory(GsonConverterFactory.create(gsonUser))
            .client(client)
            .build()

        getAPI = retrofitGet.create(ExchangeAPI::class.java)

        val retrofitPost = Retrofit.Builder()
            .baseUrl(basePostUrl)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(client)
            .build()

        postAPI = retrofitPost.create(ExchangeAPI::class.java)
    }

    fun start(exchangeTime: Long) {
        Log.d(TAG, "DataExchange start...")

        add(getAPI.getTasks(exchangeTime)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(getTasksObserver())
        )

        add(getAPI.getUsers(exchangeTime)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(getUsersObserver())
        )

        add(getAPI.getTaskUsers(exchangeTime)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(getTaskUsersObserver())
        )

        val dataGot = tasksGot.getChanges()
            .zipWith(usersGot.getChanges()) {b1, b2 -> b1 && b2}
            //usersGot.getChanges()
            .zipWith(taskUsersGot.getChanges()) {b1, b2 -> b1 && b2}
        val onDataGot = object: Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Boolean) {
                // Все данные _получены_ с сервера => запустить выгрузку
                postData(exchangeTime)
                Log.d(TAG, "Data got")
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "Getting data failed: $e")
            }

            override fun onComplete() {}
        }
        dataGot.subscribe(onDataGot)

        // Обмен завершен
        val dataPosted = tasksPosted.getChanges()
            .zipWith(usersPosted.getChanges()) { b1, b2 -> b1 && b2 }
            .zipWith(taskUsersPosted.getChanges()) { b1, b2 -> b1 && b2 }

        val onDataPosted = object: Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: Boolean) {
                Log.d(TAG, "Exchange completed")
                dispose()
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "Posting data failed: $e")
                dispose()
            }

            override fun onComplete() {}
        }
        dataPosted.subscribe(onDataPosted)
    }

    private fun getCompositeDisposable(): CompositeDisposable {
        if (compositeDisposable.isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
        return compositeDisposable
    }

    private fun add(disposable: Disposable) {
        getCompositeDisposable().add(disposable)
    }

    fun dispose() {
        // ToDo...
        //Log.d(TAG, "Disposing data exchange")
        //getCompositeDisposable().dispose()
    }

    // GET data
    private fun getTasksObserver(): DisposableSingleObserver<List<Task>> {
        Log.d(TAG, "getTasksObserver")
        return object: DisposableSingleObserver<List<Task>>() {
            override fun onSuccess(tasks: List<Task>) {
                // ToDo Update data
                tasksGot.setBoolean(true)
                Log.d(TAG, "Tasks got successfully")
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "Tasks get: $e")
            }
        }
    }

    private fun getUsersObserver(): DisposableSingleObserver<List<Users>> {
        Log.d(TAG, "getUsersObserver")
        return object: DisposableSingleObserver<List<Users>>() {
            override fun onSuccess(tasks: List<Users>) {
                // ToDo Update data
                usersGot.setBoolean(true)
                Log.d(TAG, "Users got successfully")
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "Users get: $e")
            }
        }
    }

    private fun getTaskUsersObserver(): DisposableSingleObserver<List<TaskUsers>> {
        Log.d(TAG, "getTaskUsersObserver")
        return object: DisposableSingleObserver<List<TaskUsers>>() {
            override fun onSuccess(tasks: List<TaskUsers>) {
                // ToDo Update data
                taskUsersGot.setBoolean(true)
                Log.d(TAG, "TaskUsers got successfully")
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "TaskUsers get: $e")
            }
        }
    }

    // POST data
    fun postData(exchangeTime: Long) {
        Log.d(TAG, "Posting data...")

        val tasks: List<Task> = ToDosDatabase
            .invoke(MainApplication.getInstance()).tasksDao().getAll(exchangeTime)
        if (tasks.isEmpty())
            tasksPosted.setBoolean(true)
        else
            postTasks(tasks)

        val users: List<Users> = ToDosDatabase
            .invoke(MainApplication.getInstance()).usersDao().getAll(exchangeTime)
        if (users.isEmpty())
            usersPosted.setBoolean(true)
        else
            postUsers(users)

        val taskUsers: List<TaskUsers> = ToDosDatabase
            .invoke(MainApplication.getInstance()).taskUserDao().getAll(exchangeTime)
        if (taskUsers.isEmpty())
            taskUsersPosted.setBoolean(true)
        else
            postTaskUsers(taskUsers)
    }

    private fun postTasks(tasks: List<Task>) {
        tasks.forEach {
            postTask(it)
        }
    }

    private fun postTask(task: Task) {
        compositeDisposable.add(
            postAPI.postTasks(
                task.ts_id, task.ts_name, task.ts_type, task.ts_mark, task.ts_grade,
                task.ts_percent, task.ts_parent, task.ts_date, task.ts_note,
                task.ts_createdAt, task.ts_modifiedAt, task.ts_isDeleted)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(postTaskObserver())
        )
    }

    private fun postTaskObserver(): DisposableCompletableObserver {
        return object: DisposableCompletableObserver() {
            override fun onComplete() {
                tasksPosted.setBoolean(true)
                Log.d(TAG, "Tasks posted successfully")
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "Tasks post: $e")
            }
        }
    }

    private fun postUsers(users: List<Users>) {
        users.forEach {
            postUser(it)
        }
    }

    private fun postUser(user: Users) {
        compositeDisposable.add(
            postAPI.postUsers(
                user.us_account, user.us_owner,
                user.us_createdAt, user.us_modifiedAt, user.us_isDeleted)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(postUserObserver())
        )
    }

    private fun postUserObserver(): DisposableCompletableObserver {
        return object: DisposableCompletableObserver() {
            override fun onComplete() {
                usersPosted.setBoolean(true)
                Log.d(TAG, "Users posted successfully")
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "Users post: $e")
            }

        }
    }

    private fun postTaskUsers(taskUsers: List<TaskUsers>) {
        taskUsers.forEach {
            postTaskUser(it)
        }
    }

    private fun postTaskUser(taskUser: TaskUsers) {
        compositeDisposable.add(
            postAPI.postTaskUsers(
                taskUser.tu_taskId, taskUser.tu_userId,
                taskUser.tu_createdAt, taskUser.tu_modifiedAt, taskUser.tu_isDeleted)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(postTaskUserObserver())
        )
    }

    private fun postTaskUserObserver(): DisposableCompletableObserver {
        return object : DisposableCompletableObserver() {
            override fun onComplete() {
                taskUsersPosted.setBoolean(true)
                Log.d(TAG, "TaskUsers posted successfully")
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "TaskUsers post: $e")
            }

        }
    }
}