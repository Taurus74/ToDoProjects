package com.tausoft.todoprojects

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainApp = application as MainApplication
        if (mainApp.account == null) {
            val intent = Intent(this, FirstRunActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            finish()
            startActivity(intent)
        }
        else {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            finish()
            startActivity(intent)
        }
    }

}