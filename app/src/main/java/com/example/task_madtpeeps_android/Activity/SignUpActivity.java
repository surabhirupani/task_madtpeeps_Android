package com.example.task_madtpeeps_android.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.task_madtpeeps_android.Database.AppDatabase;
import com.example.task_madtpeeps_android.Database.DAO;
import com.example.task_madtpeeps_android.Model.User;
import com.example.task_madtpeeps_android.R;

public class SignUpActivity extends AppCompatActivity {
    TextView tv_login;
    Button btn_register;
    private EditText et_fullname, et_username, et_email, et_pwd;
    private DAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dao = AppDatabase.getDb(this).getDAO();
        tv_login = findViewById(R.id.tv_login);
        btn_register = findViewById(R.id.btn_register);
        et_pwd = findViewById(R.id.et_pwd);
        et_fullname = findViewById(R.id.et_fullname);
        et_username = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_email);

        initToolbar();

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFields();
            }
        });
    }

    private void validateFields() {
        boolean isError = false;
        if (TextUtils.isEmpty(et_fullname.getText().toString().trim())) {
            et_fullname.setError("Please enter full name");
            isError = true;
        }
        if (TextUtils.isEmpty(et_username.getText().toString().trim())) {
            et_username.setError("Please enter username");
            isError = true;
        }
        if (TextUtils.isEmpty(et_pwd.getText().toString().trim())) {
            et_pwd.setError("Please enter password");
            isError = true;
        }
        if (TextUtils.isEmpty(et_email.getText().toString().trim())) {
            et_email.setError("please enter email");
            isError = true;
        }
        if (isError) return;

        if (dao.signUpControl(et_username.getText().toString(), et_email.getText().toString()) == 0) {
            User user = new User();
            user.setUserMail(et_email.getText().toString());
            user.setUserName(et_username.getText().toString());
            user.setUserFullName(et_fullname.getText().toString());
            user.setUserPassword(et_pwd.getText().toString());
            dao.insertUser(user);
            Toast.makeText(getApplicationContext(), "Sign Up Successfully!", Toast.LENGTH_LONG).show();
            Intent i = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "User already registered!", Toast.LENGTH_LONG).show();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}