package com.tausoft.todoprojects

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tausoft.todoprojects.settings.SettingsFragment
import com.tausoft.todoprojects.viewModels.UserViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers


class SettingsActivity : AppCompatActivity() {
    private lateinit var mainApp: MainApplication
    private lateinit var viewModel: UserViewModel
    private lateinit var signHelper: GoogleSignHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        mainApp = MainApplication.getInstance()
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]
        signHelper = GoogleSignHelper(this)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()
    }

    fun doLogout() {
        googleSignOut()
        revokeAccess()
    }

    // https://developers.google.com/identity/sign-in/android/disconnect
    // 1. Выйти из аккаунта
    private fun googleSignOut() {
        signHelper.signOut()
            .addOnCompleteListener(this) {
                Observable.just(mainApp)
                    .subscribeOn(Schedulers.io())
                    .subscribe {
                        val user = viewModel.getUser(it.account?.email!!)
                        viewModel.setInactive(user.us_account)
                    }
                Toast.makeText(this, R.string.user_logout, Toast.LENGTH_SHORT).show()
            }
    }

    // 2. Отключить учетную запись Google от приложения
    private fun revokeAccess() {
        signHelper.revokeAccess()
            .addOnCompleteListener {
                val intent = Intent(applicationContext, FirstRunActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
    }

}