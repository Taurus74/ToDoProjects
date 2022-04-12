package com.tausoft.todoprojects.data

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "good_tasks", primaryKeys = ["gt_good_id", "gt_task_id"])
data class GoodTasks (
    // Списки покупок
    @ColumnInfo(name = "gt_good_id")
    var gt_goodId: String = "",
    @ColumnInfo(name = "gt_task_id")
    var gt_taskId: String = "",
    var gt_createdAt: Long = 0,
    var gt_modifiedAt: Long = 0,
    var gt_isDeleted: Boolean = false
) {
    init {
        val time = System.currentTimeMillis()
        gt_createdAt = time
        gt_modifiedAt = time
    }
}