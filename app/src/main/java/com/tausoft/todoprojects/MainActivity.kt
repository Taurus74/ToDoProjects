package com.tausoft.todoprojects

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tausoft.todoprojects.adapters.TasksAdapter
import com.tausoft.todoprojects.data.Task
import com.tausoft.todoprojects.data.TaskType
import com.tausoft.todoprojects.network.ConnectionType
import com.tausoft.todoprojects.network.NetworkMonitor
import com.tausoft.todoprojects.sync.SyncHelper
import com.tausoft.todoprojects.viewModels.TasksViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.util.*

class MainActivity : AppCompatActivity(), OnFolderClickListener {
    companion object {
        private const val REQUEST_LOCATION = 1
    }

    private lateinit var list : RecyclerView
    private lateinit var adapter: TasksAdapter
    private lateinit var viewModel: TasksViewModel
    private lateinit var userId: String
    private lateinit var popup: PopupMenu
    private var visibleItemPosition = 0

    private val taskList: MutableList<Task> = mutableListOf()

    // BroadcastReceiver для получения апдейтов о состоянии сетевых подключений
    // ToDo - наблюдать
    private val monitor = NetworkMonitor(this)

    // IntentFilter подписки на обновления сетевых подключений
    private val filter = IntentFilter(
        ConnectivityManager.CONNECTIVITY_ACTION
    )

    private var connectionType = ConnectionType.NONE
    private var ssid = ""

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        setupPopUp(fab)
        fab.setOnClickListener {
            val intent = Intent(this, TaskActivity::class.java)
            startActivity(intent)
        }
        fab.setOnLongClickListener {
            popup.show()
            true
        }

        userId = getAccount()

        viewModel = ViewModelProvider(this)[TasksViewModel::class.java]
        viewModel
            .getAllTasks()
            .observe(this) {
                // Обновить данные
                taskList.clear()
                taskList.addAll(0, getChildren("", it))
                adapter.setItems(taskList)
                adapter.notifyDataSetChanged()

                // Восстановить позицию видимого элемента
                (list.layoutManager as LinearLayoutManager).scrollToPosition(visibleItemPosition)
            }
        setupRecyclerView()

        SyncHelper(this).initAutoSync()
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION)
        else
            // Подписаться на получение обновлений
            registerReceiver(monitor, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(monitor)
    }

    override fun onDestroy() {
        super.onDestroy()
        list.adapter = null
    }

    fun networkStatus(connectionType: ConnectionType, ssid: String) {
        this.connectionType = connectionType
        this.ssid = ssid
    }

    private fun getChildren(taskId: String, tasks: List<Task>): MutableList<Task> {
        val result = mutableListOf<Task>()
        val children = tasks.filter { it.ts_parent == taskId }
        for (item in children) {
            result.add(result.size, item)
            if (item.ts_expanded) {
                result.addAll(result.size, getChildren(item.ts_id, tasks))
            }
        }
        return result
    }

    private fun setupPopUp(fab: FloatingActionButton) {
        popup = PopupMenu(this, fab)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.popup_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_add_folder -> {
                    getUserString(it.itemId, R.string.get_folder_name)
                    true
                }
                R.id.menu_add_project -> {
                    getUserString(it.itemId, R.string.get_project_name)
                    true
                }
                R.id.menu_add_shop_list -> {
                    getUserString(it.itemId, R.string.get_shop_list_name)
                    true
                }
                else -> {
                    true
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var intent: Intent? = null
        when (item.itemId) {
            R.id.menu_add_folder -> {
                getUserString(item.itemId, R.string.get_folder_name)
            }
            R.id.menu_add_project -> {
                getUserString(item.itemId, R.string.get_project_name)
            }
            R.id.menu_add_shop_list -> {
                getUserString(item.itemId, R.string.get_shop_list_name)
            }
            R.id.menu_shops -> intent = Intent(this, ShopsActivity::class.java)
            R.id.menu_goods -> intent = Intent(this, GoodsActivity::class.java)
            R.id.menu_settings -> intent = Intent(this, SettingsActivity::class.java)
            R.id.menu_about -> intent = Intent(this, AboutAuthActivity::class.java)
        }
        if (intent == null) return super.onOptionsItemSelected(item) else startActivity(intent)
        return true
    }

    private fun createTask(taskType: TaskType, taskName: String) {
        val task = Task(
            UUID.randomUUID().toString(),
            taskName,
            taskType.ordinal
        )
        viewModel.insert(task, userId)
    }

    private fun setupRecyclerView() {
        list = findViewById(R.id.tasks_list)
        list.layoutManager = LinearLayoutManager(this)
        list.addItemDecoration(
            DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL
            )
        )

        adapter = TasksAdapter(viewModel, taskList, this)
        list.adapter = adapter
        setRecyclerViewScrollListener()

        val dragHandler = object: DragManageCallback(this,
            0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val taskId = (viewHolder as TasksAdapter.ViewHolder).taskId
                val taskName = viewHolder.taskName.text

                viewModel.delete(taskId, userId)

                Snackbar.make(list, taskName, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) {
                        viewModel.undelete(taskId, userId)
                    }.show()
            }
        }
        ItemTouchHelper(dragHandler)
            .attachToRecyclerView(list)
    }

    // Запоминать позицию первого видимого элемента
    private fun setRecyclerViewScrollListener() {
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                visibleItemPosition = if (taskList.size > 0)
                    (list.layoutManager as LinearLayoutManager)
                        .findFirstCompletelyVisibleItemPosition()
                else
                    -1
            }
        })
    }

    private fun getAccount(): String {
        val mainApp = application as MainApplication
        return mainApp.account?.email ?: ""
    }

    private fun getUserString(itemId: Int, titleId: Int) {
        val dialogLayout = layoutInflater.inflate(R.layout.alert_dialog_with_edittext, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editText)

        AlertDialog.Builder(this)
            .setTitle(resources.getString(titleId))
            .setView(dialogLayout)
            .setPositiveButton("OK") { _, _ ->
                when (itemId) {
                    R.id.menu_add_folder -> {
                        createTask(TaskType.FOLDER, editText.text.toString())
                    }
                    R.id.menu_add_project -> {
                        createTask(TaskType.PROJECT, editText.text.toString())
                    }
                    R.id.menu_add_shop_list -> {
                        createTask(TaskType.SHOP_LIST, editText.text.toString())
                    }
                }
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                closeContextMenu()
            }
            .show()
    }

    override fun onFolderClick(taskId: String, expanded: Boolean) {
        viewModel.setExpanded(taskId, expanded)
    }

}