package com.alaeri.cats.app.cats.db

import androidx.room.TypeConverter
import java.lang.RuntimeException

/**
 * Created by Emmanuel Requier on 19/04/2020.
 */
class BreedsConverter {

    @TypeConverter
    fun listToString(breeds: List<String>?): String? {
        if(breeds!= null && breeds.any { it.contains(",") }){
            throw RuntimeException("Can not serialize breed")
        }
        return breeds?.joinToString(",")
    }

    @TypeConverter
    fun stringToList(breedsString: String?): List<String>? {
        return breedsString?.split(",")
    }
}