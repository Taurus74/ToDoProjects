package com.tausoft.todoprojects.settings

import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.work.WorkManager
import com.tausoft.todoprojects.MainApplication
import com.tausoft.todoprojects.R
import com.tausoft.todoprojects.SettingsActivity
import com.tausoft.todoprojects.sync.SyncHelper

class SettingsFragment: PreferenceFragmentCompat() {
    private val mContext = MainApplication.getInstance().baseContext

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Обработка выхода из аккаунта
        val accountPreference: Preference? = findPreference("KEY_ACCOUNT")
        accountPreference?.title = getAccount()
        accountPreference?.setOnPreferenceClickListener {
            (activity as SettingsActivity).doLogout()
            true
        }

        // ToDo - You should use a SummaryProvider instead
        // Изменение первичного цвета интерфейса
        val palettePreference: Preference? = findPreference("KEY_PALETTE")
        palettePreference?.setOnPreferenceClickListener {
            setColorPrimary(palettePreference.key)
            true
        }

        // Обработка изменения настроек синхронизации
        // Переключение синхронизации
        val autoSync: Preference? = findPreference("KEY_AUTO_SYNC")
        autoSync?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                if (newValue as Boolean)
                    startAutoSync()
                else
                    stopAutoSync()
                true
            }

        // Изменение времени синхронизации
        val autoSyncInterval: Preference? = findPreference("KEY_AUTO_SYNC_INTERVAL")
        autoSyncInterval?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ ->
                restartAutoSync()
                true
            }

        val startSync: Preference? = findPreference("KEY_START_SYNC")
        startSync?.setOnPreferenceClickListener {
            SyncHelper(mContext).initRequiredSync()
            true
        }
    }

    private fun getAccount(): String {
        val mainApp = MainApplication.getInstance()
        return when (val account = mainApp.account) {
            null -> resources.getText(R.string.no_google_account).toString()
            else -> account.email.toString()
        }
    }

    private fun setColorPrimary(key: String?) {
        // ToDo
    }

    // Запустить периодическую синхронизацию
    private fun startAutoSync() {
        SyncHelper(mContext).initPeriodicSync()
        Log.d(MainApplication.TAG, "Start periodic sync...")
    }

    // Остановить ранее запущенную периодическую синхронизацию
    private fun stopAutoSync() {
        WorkManager.getInstance(mContext)
            .cancelAllWorkByTag(SyncHelper.SYNC_TAG)
        Log.d(MainApplication.TAG, "Stop periodic sync...")
    }

    // Перезапустить периодическую синхронизацию
    private fun restartAutoSync() {
        stopAutoSync()
        startAutoSync()
    }
}