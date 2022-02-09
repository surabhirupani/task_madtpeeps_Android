package com.example.task_madtpeeps_android.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.task_madtpeeps_android.Database.AppDatabase;
import com.example.task_madtpeeps_android.Database.DAO;
import com.example.task_madtpeeps_android.Model.User;
import com.example.task_madtpeeps_android.R;
import com.example.task_madtpeeps_android.Utils;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DAO dao;
    private User user;
    private TextView tvContinuesCount;
    private TextView tvCompletedTask;
    private TextView tvExpiredCount;
    private ImageView iv_logout;
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
        iv_logout = findViewById(R.id.iv_logout);

        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
    }

    private void logoutUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int i) {
                di.dismiss();
                SharedPreferences preferences = getSharedPreferences(Utils.APP_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(Utils.loginControlKey);
                editor.remove(Utils.loginUserNameKey);
                editor.remove(Utils.loginUserPassword);
                editor.apply();

                Intent loginSuccessIntent = new Intent(MainActivity.this, LoginActivity.class);
                loginSuccessIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                loginSuccessIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginSuccessIntent);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}