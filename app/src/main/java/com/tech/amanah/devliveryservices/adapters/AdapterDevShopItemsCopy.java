package com.tech.amanah.devliveryservices.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tech.amanah.Application.MyApplication;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.TouchImageView;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.databinding.AdapterDevshopItemsBinding;
import com.tech.amanah.databinding.ItemDetailDialogBinding;
import com.tech.amanah.devliveryservices.activities.ShopItemsAct;
import com.tech.amanah.devliveryservices.activities.ShopItemsCopyAct;
import com.tech.amanah.devliveryservices.activities.setDeliveryNormailAct;
import com.tech.amanah.shops.models.ModelShopItems;
import com.tech.amanah.taxiservices.models.ModelLogin;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterDevShopItemsCopy extends RecyclerView.Adapter<AdapterDevShopItemsCopy.MyViewHolder> {

    Context mContext;
    ArrayList<ModelShopItems.Result> shopItemsList;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private double itemTotal;
    int itemCount = 0;
    String storeId;

    public AdapterDevShopItemsCopy(Context mContext, ArrayList<ModelShopItems.Result> shopItemsList, String storeId) {
        this.mContext = mContext;
        this.shopItemsList = shopItemsList;
        this.storeId = storeId;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterDevshopItemsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.adapter_devshop_items, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.binding.setData(shopItemsList.get(position));
        //  holder.binding.tvPrice.setText(AppConstant.CURRENCY + " " + shopItemsList.get(position).getItem_price());


        if(!shopItemsList.get(position).getDiscount().equals("0")) {
            holder.binding.tvNewPrice.setVisibility(View.VISIBLE);
            //  holder.binding.tvDiscount.setVisibility(View.VISIBLE);
            holder.binding.tvOldPrice.setTextColor(mContext.getResources().getColor(R.color.red));
            double oldPrice =  Double.parseDouble(shopItemsList.get(position).getItem_price());
            holder.binding.tvOldPrice.setText(AppConstant.CURRENCY +" " + String.format("%.2f", oldPrice));
            holder.binding.tvOldPrice.setPaintFlags(holder.binding.tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            //  holder.binding.tvDiscount.setText("-"+arrayList.get(position).discount + "% Off");
            holder.binding.tvNewPrice.setText(AppConstant.CURRENCY +" " + String.format("%.2f", Double.parseDouble(shopItemsList.get(position).getPriceWithDiscount())) + " Discount of " + shopItemsList.get(position).getDiscount() + "%");

        }
        else {

            holder.binding.tvOldPrice.setText(AppConstant.CURRENCY +" " + String.format("%.2f", Double.parseDouble(shopItemsList.get(position).getItem_price())));
            holder.binding.tvOldPrice.setTextColor(mContext.getResources().getColor(R.color.red));
            holder.binding.tvNewPrice.setVisibility(View.GONE);
            //   holder.binding.tvDiscount.setVisibility(View.GONE);

        }


        Glide.with(mContext)
                .load(shopItemsList.get(position).getItem_image())
                .into(holder.binding.ivImage);

        holder.binding.ivImage.setOnClickListener(v -> {
            imageShowFullscreenDialog(shopItemsList.get(position).getItem_image());
        });

        holder.binding.ivAddItems.setOnClickListener(v -> {
            openItemDetailDialog(shopItemsList.get(position));
        });

        if(position==0){
            holder.binding.tvMostSold.setVisibility(View.VISIBLE);
        }
        else holder.binding.tvMostSold.setVisibility(View.GONE);


    }

    private void openItemDetailDialog(ModelShopItems.Result data) {

        itemTotal = 0.0;
        itemCount = 1;

        ItemDetailDialogBinding dialogBinding = DataBindingUtil.
                inflate(LayoutInflater.from(mContext)
                        , R.layout.item_detail_dialog,
                        null, false);

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(dialogBinding.getRoot());

        Glide.with(mContext).load(data.getItem_image()).into(dialogBinding.ivItemImage);

        dialogBinding.tvItemName.setText(data.getItem_name());
        if(!data.getDiscount().equals("0")) {
            itemTotal = Double.parseDouble(data.getPriceWithDiscount());
            dialogBinding.tvPrice.setText(AppConstant.CURRENCY + " " + data.getPriceWithDiscount());
        }
        else {
            itemTotal = Double.parseDouble(data.getItem_price());
            dialogBinding.tvPrice.setText(AppConstant.CURRENCY + " " + data.getItem_price());

        }


        dialogBinding.tvAddItem.setOnClickListener(v -> {
         /*   HashMap<String, String> params = new HashMap<>();
            params.put("user_id", modelLogin.getResult().getId());
            params.put("shop_id", storeId);
            params.put("item_id", data.getId());
            params.put("quantity", String.valueOf(itemCount));
            mContext.startActivity(new Intent(mContext, setDeliveryNormailAct.class)
                    .putExtra("cartParams", params));*/

             addToCartApi(dialog, data, String.valueOf(itemCount));
            dialog.dismiss();
        });

        dialogBinding.ivPlus.setOnClickListener(v -> {
            itemCount++;
            dialogBinding.tvQuantity.setText(String.valueOf(itemCount));
            if(!data.getDiscount().equals("0")) {
                itemTotal = Double.parseDouble(data.getPriceWithDiscount()) * itemCount;
            }
            else {
                itemTotal = Double.parseDouble(data.getItem_price()) * itemCount;
            }
            dialogBinding.tvPrice.setText(AppConstant.CURRENCY + " " + itemTotal);
        });

        dialogBinding.ivMinus.setOnClickListener(v -> {
            if (itemCount > 1) {
                itemCount--;
                dialogBinding.tvQuantity.setText(String.valueOf(itemCount));
                if(!data.getDiscount().equals("0")) {
                    itemTotal = Double.parseDouble(data.getPriceWithDiscount()) * itemCount;
                }
                else {
                    itemTotal = Double.parseDouble(data.getItem_price()) * itemCount;
                }
                dialogBinding.tvPrice.setText(AppConstant.CURRENCY + " " + itemTotal);
            }
        });

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    private void addToCartApi(Dialog dialog, ModelShopItems.Result data, String quantity) {
        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("shop_id", storeId);
        param.put("item_id", data.getId());
        param.put("quantity", quantity);
        param.put("address","");
        param.put("lat", "");
        param.put("lon", "");
        param.put("landmark","");
        param.put("receiver_name","");
        param.put("receiver_number","");


        Log.e("add more api", "param = " + param.toString());

        Call<ResponseBody> call = api.addToCartMoreApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("add more api", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Log.e("add more api", "responseString = " + responseString);
                        Log.e("add more api", "response = " + response);
                        Log.e("add more api", "count = " + jsonObject.getInt("count"));
                        dialog.dismiss();
                        ((ShopItemsCopyAct) mContext).getCartCount();
                    } else if (jsonObject.getString("status").equals("2")) {
                        Log.e("add more api", "responseString = " + responseString);
                        Log.e("add more api", "response = " + response);
                        removeCartDialog();
                    } else if (jsonObject.getString("status").equals("3")) {
                        twoStoreAddedDialog();
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("add more api", "response = " + t.getMessage());
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void removeCartApi() {
        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());

        Log.e("paramparam", "param = " + param.toString());

        Call<ResponseBody> call = api.removeCartDataApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("fsdfdsfdsf", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Log.e("fsdfdsfdsf", "responseString = " + responseString);
                        Log.e("fsdfdsfdsf", "response = " + response);
                        Log.e("fsdfdsfdsf", "count = " + jsonObject.getInt("count"));
                        MyApplication.showAlert(mContext,mContext.getString(R.string.add_items_in_cart));
                    }
                } catch (Exception e) {
                    MyApplication.showAlert(mContext,mContext.getString(R.string.add_items_in_cart));
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("fsdfdsfdsf", "response = " + t.getMessage());
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void removeCartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.another_res_added_cart_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeCartApi();
                        // twoStoreAddedDialog();
                        dialog.dismiss();
                    }
                }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void twoStoreAddedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.two_stores_items_added))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void imageShowFullscreenDialog(String url) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.image_fullscreen_dialog);

        TouchImageView ivImage = dialog.findViewById(R.id.ivImage);
        ivImage.setMaxZoom(4f);

        Glide.with(mContext)
                .load(url)
                .into(ivImage);

        // Picasso.get().load(url).into(ivImage);

        dialog.show();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return shopItemsList == null ? 0 : shopItemsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        AdapterDevshopItemsBinding binding;

        public MyViewHolder(@NonNull AdapterDevshopItemsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

    }

}