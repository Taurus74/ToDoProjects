package com.tausoft.todoprojects.sync

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.tausoft.todoprojects.MainApplication.Companion.TAG
import com.tausoft.todoprojects.exchange.DataExchange

class SyncWorker(val context: Context, params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {
        try {
            // ToDo - вызов обмена, фиксация времени запуска
            DataExchange.start(0L)
            Log.d(TAG, "SyncWorker working...")
        } catch (ex: Exception) {
            // ToDo - set status Failure
            // ToDo - вызов обмена, фиксация времени ошибки
            Log.e(TAG, "SyncWorker: $ex")
            DataExchange.dispose()
            return Result.failure()
        }
        // ToDo - set status Success
        // ToDo - вызов обмена, фиксация времени завершения
//        DataExchange.dispose()
        return Result.success()
    }
}