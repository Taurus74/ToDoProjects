package com.tausoft.todoprojects.adapters

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.aconst.todoprojects.R
import com.tausoft.todoprojects.FolderActivity
import com.tausoft.todoprojects.OnFolderClickListener
import com.tausoft.todoprojects.TaskActivity
import com.tausoft.todoprojects.data.Task
import com.tausoft.todoprojects.data.TaskType
import com.tausoft.todoprojects.viewModels.TasksViewModel

class TasksAdapter(private val viewModel: TasksViewModel,
                   tasks: List<Task>,
                   private val onFolderClickListener: OnFolderClickListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mTasks: List<Task> = tasks

    companion object {
        const val TASK_ID = "TASK_ID"
        const val PARENT_ID = "PARENT_ID"
        const val TASK_TYPE = "TASK_TYPE"
    }

    internal fun setItems(tasks: List<Task>) {
        mTasks = tasks
    }

    open class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        var taskId: String = ""
        var parentId: String = ""
        var taskName: TextView = view.findViewById(R.id.task_name)

        override fun onClick(parent: View?) {
            if (parent != null) {
                val intent = Intent(parent.context, TaskActivity::class.java)
                intent.putExtra(TASK_ID, taskId)
                intent.putExtra(PARENT_ID, parentId)
                startActivity(parent.context, intent, Bundle())
            }
        }
    }

    class ViewHolderTask(view: View, private val viewModel: TasksViewModel) : ViewHolder(view) {
        var taskMark: CheckBox = view.findViewById(R.id.task_mark)
        var taskGrade: CheckBox = view.findViewById(R.id.task_grade)

        init {
            view.setOnClickListener(this)
            taskMark.setOnClickListener(this)
            taskGrade.setOnClickListener(this)
        }

        override fun onClick(parent: View?) {
            if (parent != null) {
                when (parent.id) {
                    R.id.task_mark -> {
                        viewModel.updateMark(taskId, taskMark.isChecked)
                    }
                    R.id.task_grade -> {
                        viewModel.updateGrade(taskId, if (taskGrade.isChecked) 1 else 0)
                    }
                    else -> {
                        val intent = Intent(parent.context, TaskActivity::class.java)
                        intent.putExtra(TASK_ID, taskId)
                        intent.putExtra(PARENT_ID, parentId)
                        startActivity(parent.context, intent, Bundle())
                    }
                }
            }
        }
    }

    class ViewHolderFolder(view: View, private val viewModel: TasksViewModel)
        : ViewHolder(view) {
        var taskFolder: CheckBox = view.findViewById(R.id.task_folder)
        var taskGrade: CheckBox = view.findViewById(R.id.task_grade)

        init {
            view.setOnClickListener(this)
            taskGrade.setOnClickListener(this)
        }

        override fun onClick(parent: View?) {
            if (parent != null) {
                when (parent.id) {
                    R.id.task_grade -> {
                        viewModel.updateGrade(taskId, if (taskGrade.isChecked) 1 else 0)
                    }
                    else -> {
                        val intent = Intent(parent.context, FolderActivity::class.java)
                        intent.putExtra(TASK_ID, taskId)
                        intent.putExtra(PARENT_ID, parentId)
                        intent.putExtra(TASK_TYPE, TaskType.FOLDER.ordinal)
                        startActivity(parent.context, intent, Bundle())
                    }
                }
            }
        }
    }

    class ViewHolderProject(view: View, private val viewModel: TasksViewModel)
        : ViewHolder(view) {
        var taskProject: CheckBox = view.findViewById(R.id.task_project)
        var taskGrade: CheckBox = view.findViewById(R.id.task_grade)

        init {
            view.setOnClickListener(this)
            taskGrade.setOnClickListener(this)
        }

        override fun onClick(parent: View?) {
            if (parent != null) {
                when (parent.id) {
                    R.id.task_grade -> {
                        viewModel.updateGrade(taskId, if (taskGrade.isChecked) 1 else 0)
                    }
                    else -> {
                        val intent = Intent(parent.context, FolderActivity::class.java)
                        intent.putExtra(TASK_ID, taskId)
                        intent.putExtra(PARENT_ID, parentId)
                        intent.putExtra(TASK_TYPE, TaskType.PROJECT.ordinal)
                        startActivity(parent.context, intent, Bundle())
                    }
                }
            }
        }
    }

    class ViewHolderShopList(view: View, private val viewModel: TasksViewModel)
        : ViewHolder(view) {
        var taskShopList: CheckBox = view.findViewById(R.id.task_shop_list)
        var taskGrade: CheckBox = view.findViewById(R.id.task_grade)

        init {
            view.setOnClickListener(this)
            taskGrade.setOnClickListener(this)
        }

        override fun onClick(parent: View?) {
            if (parent != null) {
                when (parent.id) {
                    R.id.task_grade -> {
                        viewModel.updateGrade(taskId, if (taskGrade.isChecked) 1 else 0)
                    }
                    else -> {
                        val intent = Intent(parent.context, FolderActivity::class.java)
                        intent.putExtra(TASK_ID, taskId)
                        intent.putExtra(PARENT_ID, parentId)
                        intent.putExtra(TASK_TYPE, TaskType.SHOP_LIST.ordinal)
                        startActivity(parent.context, intent, Bundle())
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TaskType.TODO.ordinal -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_task, parent, false)
                return ViewHolderTask(view, viewModel)
            }
            TaskType.FOLDER.ordinal -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_folder, parent, false)
                return ViewHolderFolder(view, viewModel)
            }
            TaskType.PROJECT.ordinal -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_project, parent, false)
                return ViewHolderProject(view, viewModel)
            }
            TaskType.SHOP_LIST.ordinal -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_shop_list, parent, false)
                return ViewHolderShopList(view, viewModel)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_task, parent, false)
                return ViewHolderShopList(view, viewModel)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = mTasks[position]
        when (getItemViewType(position)) {
            TaskType.TODO.ordinal -> {
                (holder as ViewHolderTask).taskId = task.ts_id
                holder.parentId = task.ts_parent
                holder.taskMark.isChecked = task.ts_mark
                holder.taskName.text = task.ts_name
                holder.taskGrade.isChecked = task.ts_grade != 0
                if (task.ts_level > 0) {
                    val layoutParams = holder.taskMark.layoutParams
                    (layoutParams as LinearLayout.LayoutParams).leftMargin = 16 * task.ts_level
                    holder.taskMark.layoutParams = layoutParams
                }

            }
            TaskType.FOLDER.ordinal -> {
                (holder as ViewHolderFolder).taskId = task.ts_id
                holder.parentId = task.ts_parent
                holder.taskFolder.isChecked = task.ts_expanded
                holder.taskName.text = task.ts_name
                holder.taskGrade.isChecked = task.ts_grade != 0
                holder.taskFolder.setOnClickListener {
                    onFolderClickListener.onFolderClick(holder.taskId, holder.taskFolder.isChecked)
                }
                if (task.ts_level > 0) {
                    val layoutParams = holder.taskFolder.layoutParams
                    (layoutParams as LinearLayout.LayoutParams).leftMargin = 16 * task.ts_level
                    holder.taskFolder.layoutParams = layoutParams
                }
            }
            TaskType.PROJECT.ordinal -> {
                (holder as ViewHolderProject).taskId = task.ts_id
                holder.parentId = task.ts_parent
                holder.taskName.text = task.ts_name
                holder.taskGrade.isChecked = task.ts_grade != 0
                holder.taskProject.setOnClickListener {
                    onFolderClickListener.onFolderClick(holder.taskId, holder.taskProject.isChecked)
                }
                if (task.ts_level > 0) {
                    val layoutParams = holder.taskProject.layoutParams
                    (layoutParams as LinearLayout.LayoutParams).leftMargin = 16 * task.ts_level
                    holder.taskProject.layoutParams = layoutParams
                }
            }
            TaskType.SHOP_LIST.ordinal -> {
                (holder as ViewHolderShopList).taskId = task.ts_id
                holder.parentId = task.ts_parent
                holder.taskName.text = task.ts_name
                holder.taskGrade.isChecked = task.ts_grade != 0
                holder.taskShopList.setOnClickListener {
                    onFolderClickListener.onFolderClick(holder.taskId, holder.taskShopList.isChecked)
                }
                if (task.ts_level > 0) {
                    val layoutParams = holder.taskShopList.layoutParams
                    (layoutParams as LinearLayout.LayoutParams).leftMargin = 16 * task.ts_level
                    holder.taskShopList.layoutParams = layoutParams
                }
            }
        }
    }

    override fun getItemViewType(position: Int) = mTasks[position].ts_type

    override fun getItemCount() = mTasks.size

}