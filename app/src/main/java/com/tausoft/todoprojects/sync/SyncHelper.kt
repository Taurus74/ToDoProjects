package com.tausoft.todoprojects.sync

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.work.*
import com.tausoft.todoprojects.MainApplication.Companion.TAG
import java.util.concurrent.TimeUnit

class SyncHelper(val context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val autoSync = prefs.getBoolean("KEY_AUTO_SYNC", false)
    private var constraints: Constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .build()

    companion object {
        const val SYNC_TAG = "PERIODIC_SYNC"
    }

    fun initAutoSync() {
        Log.d(TAG, "initAutoSync")
        if (autoSync) {
            initPeriodicSync()
        }
    }

    fun initRequiredSync() {
        Log.d(TAG, "initRequiredSync")
        val syncOneTimeRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context).enqueue(syncOneTimeRequest)
    }

    private fun getAutoSyncInterval(context: Context, prefs: SharedPreferences): Long {
        // Значение по умолчанию
        val syncIntervalDefaultRes = context.resources.getString(R.string.auto_sync_interval_default)
        val syncIntervalDefault = prefs.getInt(syncIntervalDefaultRes, 3600)
        // Значение из настроек
        val syncIntervalStr = prefs.getString("KEY_AUTO_SYNC_INTERVAL", "")
        return syncIntervalStr?.toLong() ?: syncIntervalDefault.toLong()
    }

    fun initPeriodicSync() {
        Log.d(TAG, "initPeriodicSync")
        val syncPeriodicRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            getAutoSyncInterval(context, prefs), TimeUnit.MINUTES)
            .addTag(SYNC_TAG)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context).enqueue(syncPeriodicRequest)
    }
}