package com.tausoft.todoprojects

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class GoodsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goods)
        title = resources.getString(R.string.app_name) + " - Goods"
    }
}