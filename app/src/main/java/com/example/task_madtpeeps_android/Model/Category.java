package com.example.task_madtpeeps_android.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "category")
public class Category implements Serializable {
    @PrimaryKey
    private Long categoryId;

    @ColumnInfo
    private String userId;

    @ColumnInfo
    private String categoryName;

    @ColumnInfo
    private Date categoryAddDate;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Date getCategoryAddDate() {
        return categoryAddDate;
    }

    public void setCategoryAddDate(Date categoryAddDate) {
        this.categoryAddDate = categoryAddDate;
    }
}
