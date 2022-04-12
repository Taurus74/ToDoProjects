package com.tausoft.todoprojects

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tausoft.todoprojects.adapters.ParentTasksAdapter
import com.tausoft.todoprojects.adapters.TasksAdapter
import com.tausoft.todoprojects.data.TaskType
import com.tausoft.todoprojects.data.Task
import com.tausoft.todoprojects.viewModels.TasksViewModel
import java.util.*

class TaskActivity : AppCompatActivity(), Observer<List<Task>>, View.OnClickListener,
    DialogInterface.OnClickListener {
    private var taskId: String? = ""
    private var parentId: String = ""
    private var level: Int = 0
    private var taskCreated: Long = 0

    private lateinit var viewModel: TasksViewModel
    private lateinit var adapter: ArrayAdapter<Task>

    private lateinit var taskParent: Spinner
    private lateinit var clearText: ImageButton
    private lateinit var taskName: EditText
    private lateinit var taskMark: CheckBox
    private lateinit var taskGrade: CheckBox
    private lateinit var taskDate: EditText
    private lateinit var taskNote: EditText

    private val taskList: MutableList<Task> = mutableListOf()

    // Для выбора даты в диалоге
    private var cal = Calendar.getInstance()
    // Для хранения выбранной даты или полученной из объекта перед его редактированием
    private var selectedDate = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        taskParent = findViewById(R.id.task_parent)
        clearText = findViewById(R.id.clear_text)
        taskName = findViewById(R.id.task_name)
        taskMark = findViewById(R.id.task_mark)
        taskGrade = findViewById(R.id.task_grade)
        taskDate = findViewById(R.id.task_date)
        taskNote = findViewById(R.id.task_note)

        clearText.setOnClickListener {
            taskParent.setSelection(0)
            clearText.isClickable = false
        }
        findViewById<Button>(R.id.task_ok).setOnClickListener(this)

        initDatePickerDialog()
        viewModel = ViewModelProvider(this)[TasksViewModel::class.java]

        taskId = intent.getStringExtra(TasksAdapter.TASK_ID)
        if (taskId != null && taskId!!.isNotBlank()) {
            val ids: Array<String?> = arrayOf(taskId)
            viewModel.getTask(ids).observe(this, this)
        }
        parentId = intent.getStringExtra(TasksAdapter.PARENT_ID)?: ""

        initTaskList()
    }

    private fun initTaskList() {
        viewModel
            .getAllParents()
            .observe(this) {
                taskList.clear()
                taskList.addAll(0, getChildren("", it))
                taskList.add(0, Task().emptyTask())
                initSpinner()
            }
    }

    private fun getChildren(taskId: String, tasks: List<Task>): MutableList<Task> {
        val result = mutableListOf<Task>()
        val children = tasks.filter { it.ts_parent == taskId }
        for (item in children) {
            result.add(result.size, item)
            result.addAll(result.size, getChildren(item.ts_id, tasks))
        }
        return result
    }

    private fun initSpinner() {
        adapter = ParentTasksAdapter(this, android.R.layout.simple_spinner_item, taskList)
        taskParent.adapter = adapter
        taskParent.prompt = "Parent"
        if (parentId.isNotEmpty()) {
            val task = taskList.firstOrNull { it.ts_id == parentId }
            if (task != null) {
                taskParent.setSelection(taskList.indexOf(task))
                level = task.ts_level + 1
                clearText.isClickable = task.ts_id.isNotEmpty()
            }
        }
        taskParent.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long) {
                parentId = taskList[position].ts_id
                level = taskList[position].ts_level + 1
                clearText.isClickable = position > 0
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun initDatePickerDialog() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // Запоминаем в календаре "как есть"
                dateToCal(cal, year, monthOfYear, dayOfMonth)
                // Получая дату из календаря, сдвигаем нумерацию месяцев на +1
                selectedDate = dateToInt(dayOfMonth, monthOfYear + 1, year)
                taskDate.setText(dateToStr(selectedDate))
            }

        taskDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.setButton(
                DialogInterface.BUTTON_NEUTRAL,
                resources.getText(R.string.clear_date),
                this)
            datePickerDialog.show()
        }
    }

    // Преобразовать дату, заданную числом типа Int по формату yyyyMMdd, в строку
    private fun dateToStr(date: Int): String {
        return if (date == 0)
            ""
        else {
            val year: Int = date / 10000
            val month: Int = (date - year * 10000) / 100
            val day: Int = date.mod(100)
            "%02d.%02d.%04d".format(day, month, year)
        }
    }

    // Записать в календарь cal дату, заданную числом типа Int по формату yyyyMMdd
    private fun intToCalDate(cal: Calendar, date: Int) {
        if (date > 0) {
            val year: Int = date / 10000
            val month: Int = (date - year * 10000) / 100
            val day: Int = date.mod(100)
            dateToCal(cal, year, month, day)
        }
    }

    // Записать в календарь cal дату, заданную числами типа Int (День, Месяц, Год)
    private fun dateToCal(cal: Calendar, year: Int, month: Int, day: Int) {
        cal.set(Calendar.YEAR, year)
        // В календаре нумерация месяцев сдвинута на -1
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.DAY_OF_MONTH, day)
    }

    // Преобразовать дату, заданную числами типа Int (День, Месяц, Год),
    // в число типа Int по формату yyyyMMdd
    private fun dateToInt(day: Int, month: Int, year: Int): Int {
        return year * 10000 + month * 100 + day
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.task_ok -> addTask()
        }
    }

    private fun addTask() {
        if (taskId == null) {
            taskId = UUID.randomUUID().toString()
            val task = Task(
                taskId!!,
                taskName.text.toString(),
                TaskType.TODO.ordinal,
                taskMark.isChecked,
                if (taskGrade.isChecked) 1 else 0,
                0,
                parentId,
                selectedDate,
                taskNote.text.toString()
            )
            task.ts_level = level
            viewModel.insert(task, getAccount())
        }
        else {
            val task = Task(
                taskId!!,
                taskName.text.toString(),
                TaskType.TODO.ordinal,
                taskMark.isChecked,
                if (taskGrade.isChecked) 1 else 0,
                0,
                parentId,
                selectedDate,
                taskNote.text.toString(),
                taskCreated,
                System.currentTimeMillis()
            )
            task.ts_level = level
            viewModel.update(task)
        }
        finish()
    }

    private fun getAccount(): String {
        val mainApp = application as MainApplication
        return mainApp.account?.email ?: ""
    }

    override fun onChanged(t: List<Task>?) {
        taskName.setText(t!![0].ts_name)
        taskMark.isChecked = t[0].ts_mark
        taskGrade.isChecked = t[0].ts_grade > 0
        selectedDate = t[0].ts_date
        intToCalDate(cal, selectedDate)
        taskDate.setText(dateToStr(selectedDate))
        taskNote.setText(t[0].ts_note)
        taskCreated = t[0].ts_createdAt
    }

    override fun onClick(p0: DialogInterface?, p1: Int) {
        taskDate.setText("")
        selectedDate = 0
    }

}