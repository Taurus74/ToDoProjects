package com.tausoft.todoprojects.exchange

import com.tausoft.todoprojects.data.Task
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class TaskDeserializer: JsonDeserializer<Task> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Task {
        val jsonObject = json?.asJsonObject
        return Gson().fromJson(jsonObject, Task::class.java)
    }
}