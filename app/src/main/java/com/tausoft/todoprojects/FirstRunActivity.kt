package com.tausoft.todoprojects

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tausoft.todoprojects.data.TaskType
import com.tausoft.todoprojects.data.Users
import com.tausoft.todoprojects.viewModels.TasksViewModel
import com.tausoft.todoprojects.viewModels.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

class FirstRunActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mainApp: MainApplication
    private lateinit var singHelper: GoogleSignHelper
    private lateinit var viewModel: UserViewModel

    companion object {
        private const val RC_SIGN_IN = 0
        private const val TAG = "SignIn"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_run)

        singHelper = GoogleSignHelper(this)
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val signInButton = findViewById<SignInButton>(R.id.btn_sign_in)
        signInButton.setSize(SignInButton.SIZE_STANDARD)
        signInButton.setOnClickListener(this)
        findViewById<View>(R.id.text).setOnClickListener(this)
        findViewById<View>(R.id.image).setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        mainApp = application as MainApplication
    }

    override fun onClick(v: View) {
        val intent: Intent
        when (v.id) {
            R.id.image -> {
                intent = Intent(this, AboutAuthActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_sign_in -> signIn()
            R.id.text -> startMainActivity()
        }
    }

    private fun signIn() {
        val signInIntent = singHelper.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            mainApp.account = account

            addUser(account.email!!)
            Toast.makeText(this, R.string.user_login, Toast.LENGTH_SHORT).show()

            createFoo()
            startMainActivity()
        } catch (e: ApiException) {
            Log.e(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun addUser(account: String) {
        // Проверим, есть ли такой пользователь в базе
        viewModel.count()
            .observe(this) {
                if (it == 0) {
                    // Нет - создаем новую запись
                    val user = Users(account, true)
                    viewModel.add(user)
                    Log.d(TAG, "New user added: $account")
                } else
                // Обновим существующую
                    Observable.just(mainApp)
                        .subscribeOn(Schedulers.io())
                        .subscribe {
                            viewModel.setActive(account)
                            Log.d(TAG, "Existed user updated: $account")
                        }
            }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun createFoo() {
        val userId = mainApp.account?.email
        if (userId != null) {
            val tasksViewModel = TasksViewModel()

            val folder = com.tausoft.todoprojects.data.Task(
                UUID.randomUUID().toString(),
                "Folder 1", TaskType.FOLDER.ordinal,false, 0, 0,
                "", 0, "Folder note")
            tasksViewModel.insert(folder, userId)

            val project = com.tausoft.todoprojects.data.Task(
                UUID.randomUUID().toString(),
                "Project 1", TaskType.PROJECT.ordinal,false, 0, 0,
                "", 0, "Project note")
            tasksViewModel.insert(project, userId)

            val shopList = com.tausoft.todoprojects.data.Task(
                UUID.randomUUID().toString(),
                "Shop list", TaskType.SHOP_LIST.ordinal,false, 0, 0,
                "", 0, "Shop list note")
            tasksViewModel.insert(shopList, userId)

            for (i in 1..3) {
                val subfolder = com.tausoft.todoprojects.data.Task(
                    UUID.randomUUID().toString(),
                    "Subfolder $i", TaskType.FOLDER.ordinal,false, 0, 0,
                    folder.ts_id, 0, "Subfolder note $i")
                subfolder.ts_level = 1
                tasksViewModel.insert(subfolder, userId)

                for (j in 1..3) {
                    val task = com.tausoft.todoprojects.data.Task(
                        UUID.randomUUID().toString(),
                        "Task $i.$j", TaskType.TODO.ordinal,false, 0, 0,
                        subfolder.ts_id, 0, "Task note $i.$j")
                    task.ts_level = 2
                    tasksViewModel.insert(task, userId)
                }
            }

            for (i in 1..7) {
                val task = com.tausoft.todoprojects.data.Task(
                    UUID.randomUUID().toString(),
                    "Project stage $i", TaskType.TODO.ordinal,false, 0, 0,
                    project.ts_id, 0, "Project note $i")
                task.ts_level = 1
                tasksViewModel.insert(task, userId)
            }

            for (i in 1..7) {
                val task = com.tausoft.todoprojects.data.Task(
                    UUID.randomUUID().toString(),
                    "Shop item $i", TaskType.TODO.ordinal,false, 0, 0,
                    shopList.ts_id, 0, "Shop list note $i")
                task.ts_level = 1
                tasksViewModel.insert(task, userId)
            }

        }
    }
}