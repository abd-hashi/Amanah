package com.tech.amanah.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.amanah.R;
import com.tech.amanah.databinding.AdapterMyCartStoreBinding;
import com.tech.amanah.databinding.ItemNotifictionBinding;
import com.tech.amanah.devliveryservices.adapters.AdapterMyCart;
import com.tech.amanah.devliveryservices.models.NotificationModel;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    Context context;
    ArrayList<NotificationModel.Result>arrayList;

    public NotificationAdapter(Context context, ArrayList<NotificationModel.Result> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotifictionBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(context), R.layout.item_notifiction, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       holder.binding.tvMsg.setText(arrayList.get(position).getMsg());
        holder.binding.tvDate.setText("Date : "+arrayList.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemNotifictionBinding binding;
        public MyViewHolder(@NonNull ItemNotifictionBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
