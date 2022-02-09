package com.example.task_madtpeeps_android.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.task_madtpeeps_android.Model.Category;
import com.example.task_madtpeeps_android.Model.User;

import java.util.List;

@Dao
public interface DAO {
    //Insert Querys
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(Category category);

    //Delete Querys
    @Query("DELETE FROM category WHERE categoryId = :categoryId")
    void deleteCategory(Long categoryId);

    //Select Querys
    @Query("SELECT * FROM user WHERE userName = :userName AND userPassword = :password")
    User login(String userName, String password);

    @Query("SELECT * FROM user WHERE userName = :userName")
    User loginControl(String userName);

    @Query("SELECT COUNT(*) FROM user WHERE userName = :userName OR userMail = :userMail")
    Integer signUpControl(String userName, String userMail);

    @Query("SELECT category.* FROM category WHERE userId = :userId")
    List<Category> getCategorylist(String userId);
}
