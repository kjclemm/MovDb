package edu.vt.cs3714.spring2023.retrofitrecyclerviewguide

import androidx.room.TypeConverter
import java.util.*

class DateConverter{

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }


}