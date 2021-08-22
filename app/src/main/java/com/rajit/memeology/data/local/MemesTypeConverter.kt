package com.rajit.memeology.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rajit.memeology.models.Meme

class MemesTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun memeToString(meme: Meme): String {
        return gson.toJson(meme)
    }

    @TypeConverter
    fun stringToMeme(data: String): Meme {
        val listType = object: TypeToken<Meme>() {}.type
        return gson.fromJson(data, listType)
    }
}