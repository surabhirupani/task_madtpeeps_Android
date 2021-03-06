package com.example.task_madtpeeps_android.Adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_madtpeeps_android.Interfaces.RecyclerListClickListener;
import com.example.task_madtpeeps_android.Model.Category;
import com.example.task_madtpeeps_android.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {
    private RecyclerListClickListener clickListener;
    private List<Category> items;
    private List<Integer> taskCount;
    private Context context;
    private int current_selected_idx = -1;

    public CategoryListAdapter(Context context, List<Category> items, List<Integer> taskCount, RecyclerListClickListener clickListener) {
        this.items = items;
        this.clickListener = clickListener;
        this.taskCount = taskCount;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        TextView tvListTaskCount;
        ImageView ibMore;
        ImageView ivCircleView;
        View parentView;

        ViewHolder(View v) {
            super(v);
            tvCategoryName = v.findViewById(R.id.tvCategoryName);
            tvListTaskCount = v.findViewById(R.id.tvListTaskCount);
            ibMore = v.findViewById(R.id.ibMore);
            ivCircleView = v.findViewById(R.id.ivCircleView);
            parentView = v.findViewById(R.id.parentView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_category_layout, parent, false);
        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Category categoryList = getItem(position);
        holder.tvCategoryName.setText(categoryList.getCategoryName());
        holder.tvListTaskCount.setText(String.valueOf(taskCount.get(position)));
        holder.parentView.setTag(position);
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener == null) return;
                clickListener.itemClick(v, categoryList, (int) v.getTag());
            }
        });
        holder.ibMore.setTag(position);
        holder.ibMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener == null) return;
                onMoreButtonClick(v, categoryList);
            }
        });
    }

    private void onMoreButtonClick(final View view, final Category categoryList) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                clickListener.moreItemClick(view, categoryList, (int) view.getTag(), item);
                return true;
            }
        });
        popupMenu.inflate(R.menu.menu_todolist_more);
        popupMenu.show();
    }

    public void removeData(int position) {
        items.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }

    private Category getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}