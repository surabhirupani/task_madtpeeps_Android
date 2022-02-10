package com.example.task_madtpeeps_android.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.task_madtpeeps_android.Model.Category;
import com.example.task_madtpeeps_android.Model.Task;
import com.example.task_madtpeeps_android.Model.User;

import java.util.List;

@Dao
public interface DAO {
    //Insert Querys
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(Category category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(Task task);

    //Delete Querys
    @Query("DELETE FROM category WHERE categoryId = :categoryId")
    void deleteCategory(Long categoryId);

    @Query("DELETE FROM task WHERE categoryId = :categoryId")
    void deleteTaskByCategory(Long categoryId);

    @Query("DELETE FROM task WHERE taskId = :taskId")
    void deleteTask(Long taskId);

    //Select Querys
    @Query("SELECT * FROM user WHERE userName = :userName AND userPassword = :password")
    User login(String userName, String password);

    @Query("SELECT * FROM user WHERE userName = :userName")
    User loginControl(String userName);

    @Query("SELECT COUNT(*) FROM user WHERE userName = :userName OR userMail = :userMail")
    Integer signUpControl(String userName, String userMail);

    @Query("SELECT category.* FROM category WHERE userId = :userId")
    List<Category> getCategorylist(String userId);

    @Query("SELECT COUNT(task.categoryId) FROM category " +
            " LEFT JOIN task ON task.categoryId = category.categoryId " +
            " WHERE category.userId = :userId AND CASE :countType " +
            "WHEN '0' THEN task.taskStatusCode = 0 " +
            "WHEN '1' THEN task.taskStatusCode = 1 " +
            "WHEN '-1' THEN task.taskDeadline > :expiry " +
            "END")
    int getTaskCount(Long userId, String countType, String expiry);

    @Query("SELECT * FROM task WHERE categoryId = :categoryId")
    List<Task> getTasks(String categoryId);
    
    @Query("SELECT * FROM task WHERE categoryId = :categoryId ORDER BY CASE :orderType " +
            "WHEN 'taskCreateDate' THEN taskCreateDate " +
            "WHEN 'taskName' THEN taskName " +
            "END ASC")
    List<Task> getTaskByOrder(String categoryId, String orderType);
}
