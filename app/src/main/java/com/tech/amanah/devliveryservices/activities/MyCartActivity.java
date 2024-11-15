package com.tech.amanah.devliveryservices.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.tech.amanah.Application.MyApplication;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.databinding.ActivityMyCartBinding;
import com.tech.amanah.databinding.PromoCodeDialogBinding;
import com.tech.amanah.devliveryservices.adapters.AdapterMyCart;
import com.tech.amanah.devliveryservices.models.ModelMyStoreCart;
import com.tech.amanah.taxiservices.models.ModelLogin;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCartActivity extends AppCompatActivity {

    Context mContext = MyCartActivity.this;
    ActivityMyCartBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    StringBuilder builder = new StringBuilder();
    StringBuilder builderStore = new StringBuilder();
    StringBuilder builderAmount = new StringBuilder();

    String storeAmountString = "", isCodeApply = "FALSE", appliedCode = "",deliveryCharge="0",incentiveCharge="0";
    String storeId = null;
    double itemTotal = 0.0;
    double strCouponAmount = 0.0;
    boolean isCodeApplied = false;
    private int counter = 0;
    public static String type = "";
    double itemTotal2222 = 0.0;

    ArrayList<ModelMyStoreCart.Result>arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_cart);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();

        binding.tvRemovePromoCode.setOnClickListener(v ->
                {
                    isCodeApplied = false;
                    itemTotal = itemTotal + strCouponAmount;
                    binding.toPay.setText(AppConstant.CURRENCY + " " + String.format("%.2f",itemTotal));
                    isCodeApply = "FALSE";
                    appliedCode = "";
                    strCouponAmount = 0.0;
                    binding.llCoupon.setVisibility(View.GONE);
                }
                );

    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btAdd.setOnClickListener(v -> {

            if(!isCodeApplied)
            {
                addPromoCodeDialog();
            }
            else
            {
                Toast.makeText(mContext, ""+getString(R.string.code_applied), Toast.LENGTH_SHORT).show();
            }

        });

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCartApiCall();
            }
        });

        binding.tvMore.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, ShopItemsCopyAct.class)
                    .putExtra("id",arrayList.get(0).getUser_id())
                    .putExtra("name",arrayList.get(0).getShop_name())
                    .putExtra("storeId",arrayList.get(0).getShop_id())

            );
            finish();
        });



        getCartApiCall();

        binding.btCheckout.setOnClickListener(v -> {

            if (itemTotal != 0.0) {
                if (builder == null || builder.length() == 0) {
                    Toast.makeText(mContext, getString(R.string.please_add_items_in_cart), Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, String> params = new HashMap<>();

                    String currentDate = ProjectUtil.getCurrentDate();
                    String currentTime = ProjectUtil.getCurrentTime();


                    Log.e("dsfsdfsf", "amounttotal = " + itemTotal);
                    Log.e("dsfsdfsf", "params = " + params);

                  if(arrayList.get(0).getType().equalsIgnoreCase("Custom")){
                      type ="Custom";
                      params.put("user_id", modelLogin.getResult().getId());
                      params.put("cart_id", builder.toString());
                      params.put("shop_id", "");
                      params.put("book_date", "");
                      params.put("book_time", "");
                      params.put("amount", /*storeAmountString*/builderAmount.toString());
                      params.put("apply_code", appliedCode);
                      params.put("coupon_code_status", isCodeApply);
                      params.put("amounttotal", String.valueOf(itemTotal));
                    //  params.put("address","");
                   //   params.put("lat","");
                   //   params.put("lon", "");
                   //   params.put("landmark", "");
                  //    params.put("receiver_name", "");
                 //     params.put("receiver_number","");
                      params.put("delivery_charge", deliveryCharge);
                      params.put("incentive_charges", incentiveCharge);

                      startActivity(new Intent(mContext, PaymentDevOptionAct.class)
                              .putExtra("param", params));
                  }
                  else {
                      type ="Main";
                      params.put("user_id", modelLogin.getResult().getId());
                      params.put("cart_id", builder.toString());
                      params.put("shop_id", builderStore.toString());
                      params.put("book_date", currentDate);
                      params.put("book_time", currentTime);
                      params.put("amount", /*storeAmountString*/String.format("%.2f",itemTotal2222));
                      params.put("apply_code", appliedCode);
                      params.put("coupon_code_status", isCodeApply);
                      params.put("amounttotal", String.valueOf(itemTotal));
                      params.put("delivery_charge", deliveryCharge);
                      params.put("incentive_charges", incentiveCharge);


                     // startActivity(new Intent(mContext, SetDeliveryLocationActivity.class)
                            //  .putExtra(AppConstant.STORE_BOOKING_PARAMS, params)
                     // );

                      startActivity(new Intent(mContext, PaymentDevOptionAct.class)
                              .putExtra("param", params));
                  }

                }
            } else {
                Toast.makeText(mContext, getString(R.string.please_add_item), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateCartCount() {
        // getCartApiCall();
    }

    private void addPromoCodeDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        PromoCodeDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.promo_code_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btnApply.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etCode.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_code), Toast.LENGTH_SHORT).show();
            } else {
                applyCodeApi(dialogBinding.etCode.getText().toString().trim(), dialog);
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);
        dialog.show();
    }

    private void applyCodeApi(String code, Dialog dialog) {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());
        paramHash.put("code", code);

        Log.e("paramHashparamHash", "paramHash = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.applyPromoCode(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    dialog.dismiss();
                    Log.e("asfddasfasdf", "stringResponse = " + stringResponse);

                    if (jsonObject.getString("status").equals("1")) {

                        JSONObject resultJson = jsonObject.getJSONObject("result");

                        Log.e("asdasdasdasd", "resultJson.getString(\"amount\") = " + resultJson.getString("amount"));
                        Log.e("asdasdasdasd", "parseDouble = " + (Double.parseDouble(resultJson.getString("amount")) / 100));

                        if ("PERCENT".equalsIgnoreCase(resultJson.getString("type"))) {
                            double totalDiscountInPrice = itemTotal * (Double.parseDouble(resultJson.getString("amount")) / 100);
                            double finalAmount = itemTotal - totalDiscountInPrice;
                            itemTotal = finalAmount;
                            isCodeApply = "TRUE";

                            isCodeApplied = true;
                            appliedCode = code;
                            binding.toPay.setText(AppConstant.CURRENCY + " " + String.format("%.2f",itemTotal));
                            Log.e("asdasdasdasd", "totalDiscountInPrice = " + totalDiscountInPrice);
                            Log.e("asdasdasdasd", "finalAmount = " + finalAmount);
                            MyApplication.showAlert(mContext,"Code Applied");
                        } else {
                            isCodeApply = "TRUE";
                            isCodeApplied = true;
                            appliedCode = code;

                            binding.llCoupon.setVisibility(View.VISIBLE);

                            double finalAmount = itemTotal - (Double.parseDouble(resultJson.getString("amount")));
                            Log.e("finalAmount", "finalAmount = " + finalAmount);
                            itemTotal = finalAmount;
                            binding.toPay.setText(AppConstant.CURRENCY + " " + String.format("%.2f",itemTotal));

                            strCouponAmount = (Double.parseDouble(resultJson.getString("amount")));

                            binding.coupnAmount.setText(AppConstant.CURRENCY + " " + (Double.parseDouble(resultJson.getString("amount"))));

                            MyApplication.showAlert(mContext,"Code Applied");
                        }

                        Log.e("asfddasfasdf", "response = " + response);

                    } else {
                        MyApplication.showAlert(mContext,""+getString(R.string.wroung_code_applies));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void getCartApiCall() {

        counter = 0;

        builderStore = new StringBuilder();
        builder = new StringBuilder();

        Log.e("dsfdsffs", "userId = " + modelLogin.getResult().getId());

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getStoreCartApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {

                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        Log.e("getStoreCartApiCall", "response = " + response);
                        Log.e("getStoreCartApiCall", "responseString = " + responseString);

                        ModelMyStoreCart modelMyStoreCart = new Gson().fromJson(responseString, ModelMyStoreCart.class);
                        arrayList.clear();
                        arrayList.addAll(modelMyStoreCart.getResult());
                        // storeId = modelMyStoreCart.getResult().get(0).getShop_id();
                        deliveryCharge = modelMyStoreCart.getDelivery_charge();
                        incentiveCharge = modelMyStoreCart.getIncentive_amount();
                        itemTotal2222 = Double.parseDouble(modelMyStoreCart.getTotal_amount());
                       if(arrayList.get(0).getType().equalsIgnoreCase("Custom")) {
                           itemTotal = Double.parseDouble(modelMyStoreCart.getTotal_amount());
                           binding.itemPlusDevCharges.setText(AppConstant.CURRENCY + " " +"0.00");
                           binding.devCharges.setText(AppConstant.CURRENCY + " " + String.format("%.2f",Double.parseDouble(modelMyStoreCart.getDelivery_charge())));
                           binding.toPay.setText(AppConstant.CURRENCY + " " + String.format("%.2f",itemTotal));
                           binding.tvMore.setVisibility(View.GONE);

                       }
                       else {
                           itemTotal = Double.parseDouble(modelMyStoreCart.getTotal_amount())
                                   + Double.parseDouble(modelMyStoreCart.getDelivery_charge());
                           binding.itemPlusDevCharges.setText(AppConstant.CURRENCY + " " + String.format("%.2f",Double.parseDouble(modelMyStoreCart.getTotal_amount())));
                           binding.devCharges.setText(AppConstant.CURRENCY + " " + String.format("%.2f",Double.parseDouble(modelMyStoreCart.getDelivery_charge())));
                           binding.toPay.setText(AppConstant.CURRENCY + " " + String.format("%.2f",itemTotal));

                            binding.tvMore.setVisibility(View.VISIBLE);
                       }

                        Log.e("getStoreCartApiCall", "storeId = " + storeId);

                        itemTotal = itemTotal - strCouponAmount;

                        AdapterMyCart adapterMyCart = new AdapterMyCart(mContext, modelMyStoreCart.getResult());
                        binding.rvCartItem.setAdapter(adapterMyCart);

                        if(arrayList.get(0).getType().equalsIgnoreCase("Custom")) binding.tvItemTotal.setText(getString(R.string.delivery_total));
                        else  binding.tvItemTotal.setText(getString(R.string.item_total));


                        isCodeApply = "FALSE";
                        appliedCode = "";

                        HashSet<String> storeHash = new HashSet<>();
                        HashMap<String, String> storeAmountHash = new HashMap<>();

                        for (int i = 0; i < modelMyStoreCart.getResult().size(); i++)
                            storeHash.add(modelMyStoreCart.getResult().get(i).getShop_id());

                        String[] str = new String[storeHash.size()];
                        storeHash.toArray(str);

                        for (int i = 0; i < str.length; i++) {
                            builderStore.append(str[i] + ",");
                            for (int j = 0; j < modelMyStoreCart.getResult().size(); j++) {
                                if (str[i].equals(modelMyStoreCart.getResult().get(j).getShop_id())) {
                                    counter++;
                                    storeAmountHash.put(modelMyStoreCart.getResult().get(j).getShop_id()
                                            , modelMyStoreCart.getResult().get(j).getItem_amount());
                                    Log.e("AmountAmount", "storeAmountHash j = " + j + " " + modelMyStoreCart.getResult().get(j).getItem_amount());
                                    builder.append(modelMyStoreCart.getResult().get(j).getCart_id() + ",");
                                    builderAmount.append(modelMyStoreCart.getResult().get(j).getItem_amount() + ",");

                                }
                            }
                            if (builder.length() != 0) {
                                builder = builder.deleteCharAt(builder.length() - 1);
                                builder.append("_");
                            }
                        }

                        if (builderStore.length() != 0)
                            builderStore.deleteCharAt(builderStore.length() - 1);
                        if (builder.length() != 0) builder.deleteCharAt(builder.length() - 1);

                        storeAmountString = storeAmountHash.values().toString().replaceAll("\\[|\\]|\\s", "");

                        Log.e("builderbuilder", "builder = " + builder.toString());
                        Log.e("builderbuilder", "builderStore = " + builderStore.toString());
                        Log.e("builderbuilder", "stringStoreAmount = " +
                                storeAmountHash.values().toString().replaceAll("\\[|\\]|\\s", ""));

                    } else {
                        isCodeApply = "FALSE";
                        builder = new StringBuilder();
                        builderStore = new StringBuilder();
                        builderAmount = new StringBuilder();
                        binding.itemPlusDevCharges.setText(AppConstant.CURRENCY + String.format("%.2f",0.0));
                        AdapterMyCart adapterStores = new AdapterMyCart(mContext, null);
                        binding.rvCartItem.setAdapter(adapterStores);
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
            }

        });

    }

    private void getCartItemNew() {
        Log.e("dsfdsffs", "userId = " + modelLogin.getResult().getId());

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getStoreCartApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {

                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        Log.e("getStoreCartApiCall", "response = " + response);
                        Log.e("getStoreCartApiCall", "responseString = " + responseString);

                        ModelMyStoreCart modelMyStoreCart = new Gson().fromJson(responseString, ModelMyStoreCart.class);

                        // storeId = modelMyStoreCart.getResult().get(0).getRestaurant_id();
                        itemTotal = Double.parseDouble(modelMyStoreCart.getTotal_amount());

                        Log.e("getStoreCartApiCall", "itemTotal = " + itemTotal);

                        AdapterMyCart adapterMyCart = new AdapterMyCart(mContext, modelMyStoreCart.getResult());
                        binding.rvCartItem.setAdapter(adapterMyCart);
                        binding.itemPlusDevCharges.setText(AppConstant.CURRENCY + " " + String.format("%.2f",Double.parseDouble(modelMyStoreCart.getTotal_amount())));

                        for (int i = 0; i < modelMyStoreCart.getResult().size(); i++)
                            builder.append(modelMyStoreCart.getResult().get(i).getItem_id() + ",");
                        Log.e("builderbuilder", "builder = " + builder.deleteCharAt(builder.length() - 1));

                        builder = builder.deleteCharAt(builder.length() - 1);

                    } else {
                        builder = new StringBuilder();
                        binding.itemPlusDevCharges.setText(AppConstant.CURRENCY + String.format("%.2f",0.0));
                        AdapterMyCart adapterStores = new AdapterMyCart(mContext, null);
                        binding.rvCartItem.setAdapter(adapterStores);
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
            }

        });

    }

    public void updateCartId(ArrayList<ModelMyStoreCart.Result> itemsList) {
        getCartApiCall();
//      getCartItemNew();
//        builder = new StringBuilder();
//        if(itemsList != null && itemsList.size() != 0) {
//            for(int i=0;i<itemsList.size();i++) {
//                double amount = Double.parseDouble(itemsList.get(i).getItem_price());
//                double quantity = Double.parseDouble(itemsList.get(i).getQuantity());
//                itemTotal = itemTotal + (amount * quantity);
//                builder.append(itemsList.get(i).getItem_id() + ",");
//            }
//            binding.itemPlusDevCharges.setText(AppConstant.CURRENCY + " " +itemTotal);
//            builder = builder.deleteCharAt(builder.length() - 1);
//        } else {
//            binding.itemPlusDevCharges.setText(AppConstant.CURRENCY + " " +0.0);
//        }
    }

}



