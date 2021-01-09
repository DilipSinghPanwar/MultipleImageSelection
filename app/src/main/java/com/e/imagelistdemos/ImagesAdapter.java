/*
 * Created by Dilip Singh on 6/1/21 8:28 AM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 6/1/21 8:28 AM
 */

package com.e.imagelistdemos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.e.imagelistdemos.databinding.RowImagesBinding;

import java.io.File;
import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.MyViewHolder> {

    private ArrayList<String> dataItemList;
    private ItemOnClick itemOnClick;

    public ImagesAdapter(ItemOnClick itemOnClick) {
        this.itemOnClick = itemOnClick;
    }

    public void setDataItemList(ArrayList<String> dataItemList) {
        this.dataItemList = dataItemList;
        notifyDataSetChanged();
    }

    public void removeAt(int position) {
        this.dataItemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataItemList.size());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RowImagesBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.row_images, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final String dataModel = dataItemList.get(position);
        File file = new File(dataModel);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        /*Glide.with(UnduitApplication.getContext())
                .load(dataModel)
                .signature(new ObjectKey(Long.toString(System.currentTimeMillis())))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.ivMenuIcon);*/
        holder.binding.ivMenuIcon.setImageBitmap(bitmap);
        holder.binding.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemRemoved(position);
                itemOnClick.onClicked(position);
            }
        });
        holder.bind(dataModel);
    }

    @Override
    public int getItemCount() {
        return (null != dataItemList ? dataItemList.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public RowImagesBinding binding;

        public MyViewHolder(final RowImagesBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
        }

        public void bind(String dataModel) {
//            binding.setDataModel(dataModel);
        }

    }
}