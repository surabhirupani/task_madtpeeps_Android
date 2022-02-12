package com.example.task_madtpeeps_android.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

@Entity(tableName = "task")
public class Task implements Serializable {
    @PrimaryKey
    private Long taskId;

    @ColumnInfo
    private String categoryId;

    @ColumnInfo
    private String taskName;

    @ColumnInfo
    private String taskDesc;

    @ColumnInfo
    private Date taskDeadline;

    @ColumnInfo
    private Date taskCreateDate;

    @ColumnInfo
    private int taskStatusCode;

    @ColumnInfo
    private String taskStatusName;

    @ColumnInfo
    private String taskRecordingPath;

    @ColumnInfo(name = "taskImages")
    private ArrayList<String> taskImages;

    private boolean isExpanded;//Definition For Expandable RecyclerView

    public Task() {
        isExpanded = false;
        taskStatusCode = 0;
    }

    public String getTaskRecordingPath() {
        return taskRecordingPath;
    }

    public void setTaskRecordingPath(String taskRecordingPath) {
        this.taskRecordingPath = taskRecordingPath;
    }

    public ArrayList<String> getTaskImages() {
        return taskImages;
    }

    public void setTaskImages(ArrayList<String> taskImages) {
        this.taskImages = taskImages;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public Date getTaskDeadline() {
        return taskDeadline;
    }

    public void setTaskDeadline(Date taskDeadline) {
        this.taskDeadline = taskDeadline;
    }

    public Date getTaskCreateDate() {
        return taskCreateDate;
    }

    public void setTaskCreateDate(Date taskCreateDate) {
        this.taskCreateDate = taskCreateDate;
    }

    public int getTaskStatusCode() {
        return taskStatusCode;
    }

    public void setTaskStatusCode(int taskStatusCode) {
        this.taskStatusCode = taskStatusCode;
    }

    public String getTaskStatusName() {
        return taskStatusName;
    }

    public void setTaskStatusName(String taskStatusName) {
        this.taskStatusName = taskStatusName;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
