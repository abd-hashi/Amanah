package com.tech.amanah.shops.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.databinding.AdapterDevOrdersBinding;
import com.tech.amanah.databinding.AdapterShopOrderBinding;
import com.tech.amanah.databinding.FoodOrderDetailDialogBinding;
import com.tech.amanah.devliveryservices.adapters.AdapterMyOrders;
import com.tech.amanah.devliveryservices.adapters.AdapterOrderItems;
import com.tech.amanah.devliveryservices.models.ModelStoreBooking;
import com.tech.amanah.shops.activities.StoreListener;

import java.util.ArrayList;

public class AdapterShopOrders extends RecyclerView.Adapter<AdapterShopOrders.StoreHolder> {

    Context mContext;
    ArrayList<ModelStoreBooking.Result> cartList;
    StoreListener listener;

    public AdapterShopOrders(Context mContext, ArrayList<ModelStoreBooking.Result> cartList,StoreListener listener) {
        this.mContext = mContext;
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdapterShopOrders.StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterShopOrderBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_shop_order,parent,false);
        return new AdapterShopOrders.StoreHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterShopOrders.StoreHolder holder, int position) {
        ModelStoreBooking.Result data = cartList.get(position);

        // Glide.with(mContext).load(data.getShop_front_image()).into(holder.binding.ivStoreImage);

        holder.binding.tvStoreName.setText(data.getShop_name());
        holder.binding.tvStoreAddress.setText(data.getShop_address());
        holder.binding.tvOrderId.setText(mContext.getString(R.string.order_id) +" : "+ data.getOrder_id());
        holder.binding.tvDate.setText(data.getBook_date());
        holder.binding.tvTime.setText(data.getBook_time());
        holder.binding.tvStatus.setText(data.getStatus());
        holder.binding.tvOrderAmt.setText(AppConstant.CURRENCY + " " + data.getAmount());

        holder.binding.tvDetails.setOnClickListener(v -> {
            openOrderDetailDialog(data);
        });

        if(data.getDriver_name().equals("")) holder.binding.tvCancel.setVisibility(View.VISIBLE);
        else holder.binding.tvCancel.setVisibility(View.GONE);

        holder.binding.tvCancel.setOnClickListener(v -> {
            listener.onStore(position,data);
        });


    }

    private void openOrderDetailDialog(ModelStoreBooking.Result data) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        FoodOrderDetailDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.food_order_detail_dialog,null,false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.itemsTotal.setText(AppConstant.CURRENCY + " " + data.getAmount());
        dialogBinding.tvDevAddress.setText(data.getAddress());
        dialogBinding.tvOrderId.setText(data.getOrder_id());
        dialogBinding.tvDeliveryCharge.setText(AppConstant.CURRENCY + " " +data.getDelivery_charge());

        dialogBinding.payType.setText(data.getPayment_type());
        if(data.getDriver_name() == null || data.getDriver_name().equals("")) {
          dialogBinding.rlContact.setVisibility(View.GONE);
            dialogBinding.layName.setVisibility(View.GONE);

        } else {
            dialogBinding.rlContact.setVisibility(View.VISIBLE);
            dialogBinding.layName.setVisibility(View.VISIBLE);
        }
        dialogBinding.cusName.setText(data.getDriver_name());

        AdapterOrderItems adapterOrderItems = new AdapterOrderItems(mContext,data.getCart_list());
        dialogBinding.rvItems.setAdapter(adapterOrderItems);

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.ivCall.setOnClickListener(v -> {
            ProjectUtil.call(mContext,data.getDriver_mobile());
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return cartList == null?0:cartList.size();
    }

    public class StoreHolder extends RecyclerView.ViewHolder{

        AdapterShopOrderBinding binding;

        public StoreHolder(AdapterShopOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}
