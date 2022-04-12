package com.tausoft.todoprojects.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tausoft.todoprojects.MainActivity

class NetworkMonitor(private val activity: MainActivity): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val utility = Utility(context)
            val connectionType = utility.getConnectivityState()
            var ssid = ""
            if (connectionType == ConnectionType.TYPE_WIFI) {
                ssid = utility.getSSID()
            }
            activity.networkStatus(connectionType, ssid)
        }
    }
}