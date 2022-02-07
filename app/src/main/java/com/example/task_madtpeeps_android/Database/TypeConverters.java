package com.example.task_madtpeeps_android.Database;

import androidx.room.TypeConverter;

import java.util.Date;

class TypeConverters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
