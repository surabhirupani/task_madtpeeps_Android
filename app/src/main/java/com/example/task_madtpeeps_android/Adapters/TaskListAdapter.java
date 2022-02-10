package com.example.task_madtpeeps_android.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_madtpeeps_android.Interfaces.RecyclerListItemClick;
import com.example.task_madtpeeps_android.Model.Task;
import com.example.task_madtpeeps_android.R;
import com.example.task_madtpeeps_android.utils.Tools;
import com.example.task_madtpeeps_android.utils.ViewAnimation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private RecyclerListItemClick clickListener;
    private List<Task> items;
    private SimpleDateFormat sdf;
    private Context context;

    public TaskListAdapter(List<Task> items, RecyclerListItemClick clickListener) {
        this.items = items;
        this.clickListener = clickListener;
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName;
        TextView tvItemStatus;
        TextView tvItemDeadlineDate;
        TextView tvItemCreateDate;
        TextView tvRemainingDay;
        View layoutEnded;
        View layoutEdit;
        View layoutDelete;
        View parentView;
        View expandView;
        View ivExpandLogo;
        View llActionLayout;

        ViewHolder(View v) {
            super(v);
            tvItemName = v.findViewById(R.id.tvItemName);
            tvItemStatus = v.findViewById(R.id.tvItemStatus);
            tvItemDeadlineDate = v.findViewById(R.id.tvItemDeadlineDate);
            tvItemCreateDate = v.findViewById(R.id.tvItemCreateDate);
            layoutEnded = v.findViewById(R.id.layoutEnded);
            layoutEdit = v.findViewById(R.id.layoutEdit);
            layoutDelete = v.findViewById(R.id.layoutDelete);
            parentView = v.findViewById(R.id.parentView);
            expandView = v.findViewById(R.id.expandView);
            ivExpandLogo = v.findViewById(R.id.ivExpandLogo);
            llActionLayout = v.findViewById(R.id.llActionLayout);
            tvRemainingDay = v.findViewById(R.id.tvRemainingDay);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_task_layout, parent, false);
        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Task task = getItem(position);
        holder.tvItemName.setText(task.getTaskName());
//        holder.tvItemDeadlineDate.setText(sdf.format(task.getTaskDeadline()));
        holder.tvItemDeadlineDate.setText("dgshfs");
        holder.tvItemCreateDate.setText(sdf.format(task.getTaskCreateDate()));
//        Calendar remainingTime = Calendar.getInstance();
//        remainingTime.setTimeInMillis(task.getTaskDeadline().getTime() - Calendar.getInstance().getTimeInMillis());
//
//
//        Calendar taskTime = Calendar.getInstance();
//        taskTime.setTimeInMillis(task.getTaskDeadline().getTime() - task.getTaskCreateDate().getTime());


        holder.layoutEnded.setTag(position);
        holder.layoutEnded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener == null) return;
                clickListener.endTask(v, task, (int) v.getTag());
            }
        });


        holder.layoutEdit.setTag(position);
        holder.layoutEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener == null) return;
                clickListener.editTask(v, task, (int) v.getTag());
            }
        });


        holder.layoutDelete.setTag(position);
        holder.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener == null) return;
                clickListener.deleteTask(v, task, (int) v.getTag());
            }
        });


        holder.parentView.setTag(position);
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean show = toggleLayoutExpand(!task.isExpanded(), holder.ivExpandLogo, holder.expandView);
                task.setExpanded(show);
            }
        });

        if (task.getTaskStatusCode() != 0) {
            holder.llActionLayout.setVisibility(View.GONE);
            holder.tvRemainingDay.setVisibility(View.GONE);
            holder.tvItemStatus.setText("Completed");
        } else {
            holder.llActionLayout.setVisibility(View.VISIBLE);
            holder.tvRemainingDay.setVisibility(View.VISIBLE);
//            holder.tvRemainingDay.setText(String.valueOf(remainingTime.get(Calendar.DAY_OF_YEAR))+" days left");
           holder.tvRemainingDay.setText("1 day left");
//            if (remainingTime.before(Calendar.getInstance())) {
//                holder.tvItemStatus.setText("Continues");
//                holder.tvRemainingDay.setTextColor(context.getResources().getColor(R.color.grey_40));
//            } else {
//                holder.tvItemStatus.setText("Expired");
//                holder.tvRemainingDay.setTextColor(context.getResources().getColor(R.color.red_500));
//            }
            holder.parentView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        if (task.isExpanded()) {
            holder.expandView.setVisibility(View.VISIBLE);
        } else {
            holder.expandView.setVisibility(View.GONE);
        }

        Tools.toggleArrow(task.isExpanded(), holder.ivExpandLogo, false);
    }

    private boolean toggleLayoutExpand(boolean show, View view, View lyt_expand) {
        Tools.toggleArrow(show, view);
        if (show) {
            ViewAnimation.expand(lyt_expand);
        } else {
            ViewAnimation.collapse(lyt_expand);
        }
        return show;
    }

    private Task getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
