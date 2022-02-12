package com.example.task_madtpeeps_android.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.task_madtpeeps_android.Adapters.CategoryListAdapter;
import com.example.task_madtpeeps_android.Database.AppDatabase;
import com.example.task_madtpeeps_android.Database.DAO;
import com.example.task_madtpeeps_android.Interfaces.RecyclerListClickListener;
import com.example.task_madtpeeps_android.Model.Category;
import com.example.task_madtpeeps_android.Model.Task;
import com.example.task_madtpeeps_android.Model.User;
import com.example.task_madtpeeps_android.R;
import com.example.task_madtpeeps_android.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DAO dao;
    private User user;
    private TextView tvContinuesCount;
    private TextView tvCompletedTask;
    private TextView tvExpiredCount;
    private ImageView iv_logout;
    private View llEmptyBox;
    private List<Category> categoryList;
    private List<Integer> taskCount;
    private CategoryListAdapter categoryListAdapter;
    private SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getExtras() != null) {
            user = new Gson().fromJson(getIntent().getStringExtra("user"), User.class);
        }

        dao = AppDatabase.getDb(this).getDAO();
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        categoryList = new ArrayList<>();
        taskCount = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Hi, "+ user.getUserFullName());
        setSupportActionBar(toolbar);

        tvContinuesCount = findViewById(R.id.tvContinuesCount);
        tvCompletedTask = findViewById(R.id.tvCompletedTask);
        tvExpiredCount = findViewById(R.id.tvExpiredCount);
        llEmptyBox = findViewById(R.id.llEmptyBox);
        iv_logout = findViewById(R.id.iv_logout);
        categoryListAdapter = new CategoryListAdapter(this, categoryList, taskCount, new RecyclerListClickListener() {
            @Override
            public void itemClick(View view, Object item, int position) {
                Category category = (Category) item;
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                intent.putExtra("user", new Gson().toJson(user));
                intent.putExtra("categoryId", category.getCategoryId());
                intent.putExtra("categoryName", category.getCategoryName());
                startActivity(intent);
            }

            @Override
            public void moreItemClick(View view, Object item, int position, MenuItem menuItem) {
                Category category = (Category) item;
                switch (menuItem.getItemId()) {
                    case R.id.action_delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Are you sure you want to delete this category?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface di, int i) {
                                di.dismiss();
                                dao.deleteTaskByCategory(category.getCategoryId());
                                dao.deleteCategory(category.getCategoryId());
                                Toast.makeText(getApplicationContext(), "Category deleted!", Toast.LENGTH_LONG).show();
                                getCategoryList();
                            }
                        });
                        builder.setNegativeButton("Cancel", null);
                        builder.show();
                        break;
                    case R.id.action_update:
                        showEditAddCategory(category);
                        break;
                }
            }

        });
        RecyclerView recyclerViewTodoList = findViewById(R.id.recyclerViewCategoryList);
        recyclerViewTodoList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTodoList.setHasFixedSize(true);
        recyclerViewTodoList.setAdapter(categoryListAdapter);

        getCategoryList();   

        FloatingActionButton floatingActionButton = findViewById(R.id.fabNewList);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditAddCategory(null);
            }
        });

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

    private void getCategoryList() {
        categoryList.clear();
        taskCount.clear();
        List<Category> categoryList1 = dao.getCategorylist(String.valueOf(user.getUserId()));
        if (categoryList1.isEmpty()) {
            llEmptyBox.setVisibility(View.VISIBLE);
        } else {
            llEmptyBox.setVisibility(View.GONE);
            categoryList.addAll(dao.getCategorylist(String.valueOf(user.getUserId())));
            for (int i=0;i<categoryList.size();i++) {
                String catId = String.valueOf(categoryList.get(i).getCategoryId());
                List<Task> todoListItems1 = dao.getTasks(catId);
                taskCount.add(todoListItems1.size());
            }
        }
        int continuesCount = dao.getTaskCount(user.getUserId(), "0");
        int completedCount = dao.getTaskCount(user.getUserId(), "1");
        int expiredCount = dao.getTaskCount(user.getUserId(), "-1");
        tvContinuesCount.setText(String.valueOf(continuesCount));
        tvCompletedTask.setText(String.valueOf(completedCount));
        tvExpiredCount.setText(String.valueOf(expiredCount));
        categoryListAdapter.notifyDataSetChanged();
    }

    private void showEditAddCategory(final Category category) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dlog_add_edit_category_layout);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText et_catname = dialog.findViewById(R.id.et_catname);
        final TextView tvHeader = dialog.findViewById(R.id.tvHeader);
        final Button buttonSave = dialog.findViewById(R.id.btnSave);
        
        if (category != null) {
            et_catname.setText(category.getCategoryName());
            tvHeader.setText("Edit Category");
            buttonSave.setText("Update");
        } else {
            et_catname.getText().clear();
            tvHeader.setText("Add Category");
            buttonSave.setText("Save");
        }
        
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        dialog.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_catname.getText().toString().trim())) {
                    et_catname.setError("Please enter category name");
                    return;
                }
                String message = "";
                Category category1;
                if (category != null) {
                    category1 = category;
                    message = "Category updated!";
                } else {
                    category1 = new Category();
                    message = "Category Added!";
                }
                category1.setUserId(String.valueOf(user.getUserId()));
                category1.setCategoryName(et_catname.getText().toString());
                category1.setCategoryAddDate(new Date());
                dao.insertCategory(category1);
                dialog.dismiss();
                getCategoryList();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getCategoryList();
    }
}