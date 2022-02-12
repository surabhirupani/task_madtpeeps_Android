package com.example.task_madtpeeps_android.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.task_madtpeeps_android.Adapters.TaskListAdapter;
import com.example.task_madtpeeps_android.Database.AppDatabase;
import com.example.task_madtpeeps_android.Database.DAO;
import com.example.task_madtpeeps_android.Interfaces.RecyclerListItemClick;
import com.example.task_madtpeeps_android.Model.Task;
import com.example.task_madtpeeps_android.Model.User;
import com.example.task_madtpeeps_android.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity {
    private DAO dao;
    private SimpleDateFormat sdf;
    private Long categoryId;
    private String categoryName, lastSearch = "";
    private View llEmptyBox;
    private List<Task> taskList, searchedLists;
    private TaskListAdapter taskListAdapter;

    private SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            System.out.println("New Text:" + newText);
            searchedLists.clear();
            taskListAdapter.notifyDataSetChanged();
            for (Task pp : taskList) {
                if (pp.getTaskName().toUpperCase().contains(newText.toUpperCase(new Locale("tr")))) {
                    searchedLists.add(pp);
                }
            }
            taskListAdapter.notifyDataSetChanged();
            lastSearch = newText;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        if (getIntent().getExtras() != null) {
            categoryId = getIntent().getLongExtra("categoryId", 0);
            categoryName = getIntent().getStringExtra("categoryName");
        }

        sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.US);
        dao = AppDatabase.getDb(this).getDAO();
        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setTitle(categoryName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        llEmptyBox = findViewById(R.id.llEmptyBox);

        FloatingActionButton floatingActionButton = findViewById(R.id.fabNewListItem);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskActivity.this, AddTaskActivity.class);
                intent.putExtra("categoryId", categoryId);
                startActivity(intent);
            }
        });

        taskList = new ArrayList<>();
        searchedLists = new ArrayList<>();
        taskListAdapter = new TaskListAdapter(searchedLists, new RecyclerListItemClick() {
            @Override
            public void endTask(View view, Object item, int position) {
                final Task task = (Task) item;
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskActivity.this);
                builder.setTitle("Complete Task");
                builder.setMessage("Are you sure you want to complete task?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface di, int i) {
                        di.dismiss();
                        task.setTaskStatusCode(1);
                        task.setExpanded(false);
                        dao.insertTask(task);
                        getTodoListItems();
                        Toast.makeText(getApplicationContext(), "Task Completed!", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }

            @Override
            public void editTask(View view, Object item, int position) {
                Task task = (Task) item;
                Intent intent = new Intent(TaskActivity.this, AddTaskActivity.class);
                intent.putExtra("task", task);
                intent.putExtra("categoryId", categoryId);
                startActivity(intent);
//                showAddListItemDialog(task);
            }

            @Override
            public void deleteTask(View view, final Object item, int position) {
                final Task task = (Task) item;
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskActivity.this);
                builder.setTitle("Delete Task");
                builder.setMessage("Are you sure you want to delete task?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface di, int i) {
                        di.dismiss();
                        dao.deleteTask(task.getTaskId());
                        getTodoListItems();
                        Toast.makeText(getApplicationContext(), "Task Deletes!", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });

        RecyclerView recyclerViewTodoList = findViewById(R.id.recyclerViewTaskItems);
        recyclerViewTodoList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTodoList.setHasFixedSize(true);
        recyclerViewTodoList.setAdapter(taskListAdapter);

        getTodoListItems();
    }

    private void getTodoListItems() {
        taskList.clear();
        searchedLists.clear();
        List<Task> todoListItems1 = dao.getTasks(String.valueOf(categoryId));
        if (todoListItems1.isEmpty())
            llEmptyBox.setVisibility(View.VISIBLE);
        else {
            llEmptyBox.setVisibility(View.GONE);
            taskList.addAll(todoListItems1);
            searchedLists.addAll(todoListItems1);
            taskListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_todolistitem, menu);
        ImageButton orderButton = (ImageButton) menu.findItem(R.id.action_order).getActionView();
        orderButton.setImageResource(R.drawable.ic_order);
        orderButton.setBackgroundColor(getResources().getColor(R.color.purple_500));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.rightMargin = 20;
        orderButton.setLayoutParams(params);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(TaskActivity.this, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        List<Task> todoListItems1 = new ArrayList<>();
                        switch (item.getItemId()) {
                            case R.id.action_createdate:
                                todoListItems1.addAll(dao.getTaskByOrder(String.valueOf(categoryId), "taskCreateDate"));
                                break;
                            case R.id.action_name:
                                todoListItems1.addAll(dao.getTaskByOrder(String.valueOf(categoryId), "taskName"));
                                break;
                        }
                        taskList.clear();
                        searchedLists.clear();
                        if (todoListItems1.isEmpty())
                            llEmptyBox.setVisibility(View.VISIBLE);
                        else {
                            llEmptyBox.setVisibility(View.GONE);
                            taskList.addAll(todoListItems1);
                            searchedLists.addAll(todoListItems1);
                        }
                        taskListAdapter.notifyDataSetChanged();
                        return true;
                    }
                });
                popupMenu.inflate(R.menu.menu_todolistitem_order);
                popupMenu.show();
            }
        });

        MenuItem searchItem = menu.findItem(R.id.searchBar);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Task");
        searchView.setOnQueryTextListener(searchListener);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchedLists.clear();
                searchedLists.addAll(taskList);
                taskListAdapter.notifyDataSetChanged();
                return false;
            }
        });
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));
        if (lastSearch != null && !lastSearch.isEmpty()) {
            searchView.setIconified(false);
            searchView.setQuery(lastSearch, false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getTodoListItems();
    }
}