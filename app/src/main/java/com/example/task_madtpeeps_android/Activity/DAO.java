package com.example.task_madtpeeps_android.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.task_madtpeeps_android.Model.User;

@Dao
public interface DAO {
    //Insert Querys
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Query("SELECT COUNT(*) FROM user WHERE userName = :userName OR userMail = :userMail")
    Integer signUpControl(String userName, String userMail);
}
