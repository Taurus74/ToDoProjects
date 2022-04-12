package com.tausoft.todoprojects.data

import androidx.room.Entity
import java.util.*

@Entity(tableName = "tasks", primaryKeys = ["ts_id"])
data class Task (
    // Задачи
    var ts_id: String = UUID.randomUUID().toString(),
    var ts_name : String = "", // текст задачи
    var ts_type: Int = 0, // тип (перечисление: задача/проект/список покупок/…)
    var ts_mark: Boolean = false, // отметка о выполнении (для задачи/покупки)
    var ts_grade: Int = 0, // признак важности [0..5]
    var ts_percent: Int = 0, // процент выполнения [0..100] (для проекта)
    var ts_parent : String = "", // ссылка на задачу-родитель
    var ts_date : Int = 0, // дата выполнения
    var ts_note : String = "", // примечание
    var ts_createdAt: Long = 0,
    var ts_modifiedAt: Long = 0,
    var ts_isDeleted: Boolean = false,
    // Свойства для поддержки отображения структуры в виде дерева
    var ts_expanded: Boolean = false, // = true - группа открыта, = false - закрыта
    var ts_level: Int = 0 // Уровень задачи. 0 - верхний
) {
    init {
        val time = System.currentTimeMillis()
        ts_createdAt = time
        ts_modifiedAt = time
    }

    override fun toString(): String {
        return ts_name
    }

    fun emptyTask(): Task {
        return Task("", "", ts_level = -1)
    }

    // ToDo - ???
    override fun equals(other: Any?): Boolean {
        return (ts_id             == (other as Task).ts_id)
                && (ts_name       == other.ts_name)
                && (ts_type       == other.ts_type)
                && (ts_mark       == other.ts_mark)
                && (ts_grade      == other.ts_grade)
                && (ts_percent    == other.ts_percent)
                && (ts_parent     == other.ts_parent)
                && (ts_date       == other.ts_date)
                && (ts_note       == other.ts_note)
                && (ts_createdAt  == other.ts_createdAt)
                && (ts_modifiedAt == other.ts_modifiedAt)
                && (ts_isDeleted  == other.ts_isDeleted)
                && (ts_expanded   == other.ts_expanded)
                && (ts_level      == other.ts_level)
    }

    override fun hashCode(): Int {
        return ts_id.hashCode()
    }
}