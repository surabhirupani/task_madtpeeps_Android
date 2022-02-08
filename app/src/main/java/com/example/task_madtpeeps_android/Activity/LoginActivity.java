package com.example.task_madtpeeps_android.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.task_madtpeeps_android.Utils;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {
    TextView tvSignUp;
    Button btn_login;
    //Room Database
    private DAO dao;

    //Login Authentication
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    //Components
    private EditText et_username, et_pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dao = AppDatabase.getDb(this).getDAO();
        preferences = getSharedPreferences(Utils.APP_NAME, MODE_PRIVATE);

        if (preferences.getBoolean(Utils.loginControlKey, false)) {
            User user = dao.loginControl(preferences.getString(Utils.loginUserNameKey, ""));
            openMainActivity(user);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        tvSignUp = findViewById(R.id.tvSignUp);
        btn_login = findViewById(R.id.btn_login);
        et_pwd = findViewById(R.id.et_pwd);
        et_username = findViewById(R.id.et_username);

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLogin();
            }
        });
    }

    private void validateLogin() {
        boolean isError = false;
        if (TextUtils.isEmpty(et_username.getText().toString().trim())) {
            et_username.setError("Please enter username");
            isError = true;
        }
        if (TextUtils.isEmpty(et_pwd.getText().toString().trim())) {
            et_pwd.setError("Please enter password");
            isError = true;
        }
        if (isError) return;
        User user = dao.login(et_username.getText().toString(), et_pwd.getText().toString());
        if (user != null) {
            Toast.makeText(getApplicationContext(), "Login Successfully!", Toast.LENGTH_LONG).show();
            createLoginSession(et_username.getText().toString());
            openMainActivity(user);
        } else {
            Toast.makeText(getApplicationContext(), "Login Failed!", Toast.LENGTH_LONG).show();
        }
    }

    private void openMainActivity(User user) {
        Bundle bundle = new Bundle();
        bundle.putString("user", new Gson().toJson(user));
        Intent loginSuccessIntent = new Intent(LoginActivity.this, MainActivity.class);
        loginSuccessIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        loginSuccessIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginSuccessIntent.putExtras(bundle);
        startActivity(loginSuccessIntent);
        finish();
    }


    public void createLoginSession(String userName) {
        editor = preferences.edit();
        editor.putBoolean(Utils.loginControlKey, true);
        editor.putString(Utils.loginUserNameKey, userName);
        editor.apply();
    }
}