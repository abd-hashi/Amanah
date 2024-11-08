package com.tech.amanah.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.databinding.ItemRideBookBinding;
import com.tech.amanah.taxiservices.models.ModelCar;

import java.util.ArrayList;

/**
 * Created by Ravindra Birla on 18,February,2021
 */

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.MyViewHolder> {

    Context context;
    ArrayList<ModelCar> arrayList;
    private onCarSelectListener listener;

    public interface onCarSelectListener {
        void onCarSelected(ModelCar car);
    }

    public CarAdapter(Context context, ArrayList<ModelCar> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public CarAdapter Callback(onCarSelectListener listener) {
        this.listener = listener;
        return this;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRideBookBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_ride_book, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.setCar(arrayList.get(position));

       // holder.binding.tvCarTotal.setText(AppConstant.CURRENCY + " " + arrayList.get(position).getTotal());
        holder.binding.tvCarTotal.setText( arrayList.get(position).getCharge()+  " " +AppConstant.CURRENCY  + " per km");
        holder.binding.tvCarTotalMain.setVisibility(View.GONE);
        holder.binding.tvCarTotalMain.setText( arrayList.get(position).getTotal()+  " " +AppConstant.CURRENCY );

        Glide.with(context).load(arrayList.get(position).getCarImage())
                .into(holder.binding.ivCar);

        holder.binding.executePendingBindings();

        holder.binding.getRoot().setOnClickListener(v -> {
            for (int i = 0; i < arrayList.size(); i++) {
                arrayList.get(i).setSelected(false);
            }
            arrayList.get(position).setSelected(true);
            listener.onCarSelected(arrayList.get(position));
            notifyDataSetChanged();
        });

        Log.e("check position===",arrayList.get(position).getCabFind());
        if(!arrayList.get(position).getCabFind().equals("no_cab")) {
            if (Integer.parseInt(arrayList.get(position).getCabFind()) >= 60) {
                int hour = Integer.parseInt(arrayList.get(position).getCabFind()) / 60;
                int min = Integer.parseInt(arrayList.get(position).getCabFind()) % 60;
                holder.binding.tvTime.setText(hour + " hour " + min + " min");
            } else {
                holder.binding.tvTime.setText(arrayList.get(position).getCabFind() + "min");

            }
        }
        else holder.binding.tvTime.setText("No Cab Found");

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemRideBookBinding binding;

        public MyViewHolder(@NonNull ItemRideBookBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }


}
