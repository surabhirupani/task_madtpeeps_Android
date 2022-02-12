package com.example.task_madtpeeps_android.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.task_madtpeeps_android.Interfaces.RecyclerListClickListener;
import com.example.task_madtpeeps_android.R;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private ArrayList<Bitmap> galleryList;
    private Context context;
    private RecyclerListClickListener clickListener;

    public ImageAdapter(Context context, ArrayList<Bitmap> galleryList,  RecyclerListClickListener clickListener) {
        this.galleryList = galleryList;
        this.context = context;
        this.clickListener = clickListener;
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_image_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageAdapter.ViewHolder viewHolder, int i) {
        viewHolder.img.setImageBitmap((galleryList.get(i)));
        viewHolder.img.setTag(i);
        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener == null) return;
                clickListener.itemClick(v, galleryList.get(i), (int) v.getTag());
            }
        });
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        public ViewHolder(View view) {
            super(view);

            img = (ImageView) view.findViewById(R.id.iv_gallery);
        }
    }
}