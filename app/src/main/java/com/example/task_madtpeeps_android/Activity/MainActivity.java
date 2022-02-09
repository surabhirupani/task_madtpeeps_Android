package com.example.task_madtpeeps_android.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.task_madtpeeps_android.Database.AppDatabase;
import com.example.task_madtpeeps_android.Database.DAO;
import com.example.task_madtpeeps_android.Model.User;
import com.example.task_madtpeeps_android.R;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DAO dao;
    private User user;

    private TextView tvContinuesCount;
    private TextView tvCompletedTask;
    private TextView tvExpiredCount;

    private View llEmptyBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getExtras() != null) {
            user = new Gson().fromJson(getIntent().getStringExtra("user"), User.class);
        }

        dao = AppDatabase.getDb(this).getDAO();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Hi, "+ user.getUserFullName());
        setSupportActionBar(toolbar);

        tvContinuesCount = findViewById(R.id.tvContinuesCount);
        tvCompletedTask = findViewById(R.id.tvCompletedTask);
        tvExpiredCount = findViewById(R.id.tvExpiredCount);
        llEmptyBox = findViewById(R.id.llEmptyBox);
    }
}