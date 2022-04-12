package com.tausoft.todoprojects.exchange

import com.tausoft.todoprojects.data.Users
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class UserDeserializer: JsonDeserializer<Users> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Users {
        val jsonObject = json?.asJsonObject
        return Gson().fromJson(jsonObject, Users::class.java)
    }
}