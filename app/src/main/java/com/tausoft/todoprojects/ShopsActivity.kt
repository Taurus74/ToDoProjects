package com.tausoft.todoprojects

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ShopsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shops)
        title = resources.getString(R.string.app_name) + " - Shops"
    }
}