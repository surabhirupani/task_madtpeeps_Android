package com.example.task_madtpeeps_android.Database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
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

    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

//    @TypeConverter
//    public static String BitMapToString(Bitmap bitmap){
//        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
//        byte [] b=baos.toByteArray();
//        String temp= Base64.encodeToString(b, Base64.DEFAULT);
//        if(temp==null)
//        {
//            return null;
//        }
//        else
//            return temp;
//    }
//
//    @TypeConverter
//    public static Bitmap StringToBitMap(String encodedString){
//        try {
//            byte[] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
//            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//            if(bitmap==null)
//            {
//                return null;
//            }
//            else
//            {
//                return bitmap;
//            }
//
//        } catch(Exception e) {
//            e.getMessage();
//            return null;
//        }
//    }

}
