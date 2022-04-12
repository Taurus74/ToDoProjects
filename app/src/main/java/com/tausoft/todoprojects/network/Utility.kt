package com.tausoft.todoprojects.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.PowerManager

class Utility(val context: Context) {
    fun getConnectivityState(): ConnectionType {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        var connectionType = ConnectionType.NONE

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val info = connectivityManager.activeNetworkInfo
            if (info != null && info.isConnected) {
                connectionType = when (info.type) {
                    ConnectivityManager.TYPE_WIFI -> ConnectionType.TYPE_WIFI
                    ConnectivityManager.TYPE_MOBILE -> ConnectionType.TYPE_MOBILE
                    else -> ConnectionType.NONE
                }
            }
        } else {
            val n = connectivityManager.activeNetwork

            if (n != null) {
                val nc = connectivityManager.getNetworkCapabilities(n)

                if (nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    connectionType = ConnectionType.TYPE_MOBILE
                } else if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    connectionType = ConnectionType.TYPE_WIFI
                }
            }
        }
        return connectionType
    }

    fun getSSID(): String {
        val wifiManager: WifiManager = context.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info: WifiInfo = wifiManager.connectionInfo
        return if (info.supplicantState == SupplicantState.COMPLETED)
            info.ssid
        else
            ""
    }

    fun checkWiFiName(wifiName: String): Boolean {
        val connectionType = getConnectivityState()
        return if (connectionType == ConnectionType.TYPE_WIFI) {
            val ssid = getSSID()
            wifiName == ssid
        } else
            false
    }

    // Проверка, когда можно отправить запрос в сеть
    fun checkPowerState(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.activeNetworkInfo

        // Использовать в сервисе
        if (info != null && info.isConnected) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return true
            } else {
                val power = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                if (!power.isDeviceIdleMode
                    || power.isIgnoringBatteryOptimizations(context.packageName)) {
                    return true
                }
            }
        }
        return false
    }
}