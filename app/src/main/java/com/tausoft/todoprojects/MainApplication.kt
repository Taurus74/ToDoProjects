package com.tausoft.todoprojects

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class MainApplication : MultiDexApplication() {
    var account: GoogleSignInAccount? = null

    companion object {
        const val TAG = "ToDoProjects"

        private var instance: MainApplication? = null
        fun getInstance() = instance!!
        const val BASE_URL = "http://192.168.1.14:140/"
        const val BASE_URL_DEBUG = "http://192.168.1.14:145/"
        var baseUrl: String? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        account = GoogleSignIn.getLastSignedInAccount(this)

        val debug = true    // ToDo autodetect
        baseUrl = when {
            debug -> {
                Log.d(TAG, "Using debug base")
                BASE_URL_DEBUG
            }
            else -> {
                Log.d(TAG, "Using working base")
                BASE_URL
            }
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}