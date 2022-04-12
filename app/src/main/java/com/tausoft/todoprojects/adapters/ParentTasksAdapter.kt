package com.tausoft.todoprojects.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.tausoft.todoprojects.R
import com.tausoft.todoprojects.data.Task
import com.tausoft.todoprojects.data.TaskType

class ParentTasksAdapter(context: Context,
                         resource: Int,
                         list: List<Task>): ArrayAdapter<Task>(context, resource, list) {
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val task = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.row_parent_task,
            parent,
            false
        )
        val image: ImageView = view.findViewById(R.id.parent_image)
        val parentName: TextView = view.findViewById(R.id.parent_name)
        if (task != null) {
            when (task.ts_type) {
                TaskType.FOLDER.ordinal -> image.setImageResource(R.drawable.folder)
                TaskType.PROJECT.ordinal -> image.setImageResource(R.drawable.project)
                TaskType.SHOP_LIST.ordinal -> image.setImageResource(R.drawable.shopping)
                else -> {}
            }
            parentName.text = task.ts_name
            if (task.ts_level > 0) {
                val layoutParams = image.layoutParams
                (layoutParams as LinearLayout.LayoutParams).leftMargin = 16 * task.ts_level
                image.layoutParams = layoutParams
            }
        }
        return view
    }
}