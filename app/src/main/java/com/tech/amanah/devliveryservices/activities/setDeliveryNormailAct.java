package com.tech.amanah.devliveryservices.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.model.LatLng;
import com.tech.amanah.Application.MyApplication;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.activities.PinLocationActivity;
import com.tech.amanah.databinding.ActivitySetDeliveryLocationBinding;
import com.tech.amanah.shops.models.ModelShopItems;
import com.tech.amanah.taxiservices.models.ModelLogin;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class setDeliveryNormailAct extends AppCompatActivity {

    Context mContext = setDeliveryNormailAct.this;
    ActivitySetDeliveryLocationBinding binding;
    private LatLng latLng;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    AlertDialog.Builder alertBuilder;
    HashMap<String, String> bookingParams = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_delivery_location);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        bookingParams = (HashMap<String, String>) getIntent().getSerializableExtra("cartParams");

        Log.e("fdsfdsfsdfds", bookingParams.toString());

        itit();

    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.etAddress.setOnClickListener(v -> {
            sharedPref.setScreenType("type","delivery");
            startActivityForResult(new Intent(mContext, PinLocationActivity.class), 222);
        });

        binding.btNext.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etAddress.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_add), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etLandMark.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enterlandmark_add), Toast.LENGTH_SHORT).show();
            }
          /*  else if (TextUtils.isEmpty(binding.etReceiverName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_receiver_name), Toast.LENGTH_SHORT).show();
            }

            else if (TextUtils.isEmpty(binding.etMobileNumber.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_receiver_mobile_number), Toast.LENGTH_SHORT).show();
            }*/

            else {
                bookingParams.put("address", binding.etAddress.getText().toString().trim()
                        + " " + binding.etLandMark.getText().toString().trim());
                bookingParams.put("lat", String.valueOf(latLng.latitude));
                bookingParams.put("lon", String.valueOf(latLng.longitude));
                bookingParams.put("drop_landmark",binding.etLandMark.getText().toString());
                //  bookingParams.put("receiver_name",binding.etReceiverName.getText().toString());
                //  bookingParams.put("receiver_number",binding.etMobileNumber.getText().toString());

                bookingParams.put("receiver_name","");
                bookingParams.put("receiver_number","");

               /* startActivity(new Intent(mContext, PaymentDevOptionAct.class)
                        .putExtra("param", bookingParams)
                );*/
                addToCartApi();

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 222) {
            String add = data.getStringExtra("add");
            Log.e("sfasfdas", "fdasfdas = 222 = " + add);
            Log.e("sfasfdas", "fdasfdas = lat = " + data.getDoubleExtra("lat", 0.0));
            Log.e("sfasfdas", "fdasfdas = lon = " + data.getDoubleExtra("lon", 0.0));
            double lat = data.getDoubleExtra("lat", 0.0);
            double lon = data.getDoubleExtra("lon", 0.0);
            latLng = new LatLng(lat, lon);
            binding.etAddress.setText(add);
        }
    }


    private void addToCartApi() {
        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

      //  HashMap<String, String> param = new HashMap<>();
     //   param.put("user_id", modelLogin.getResult().getId());
     //   param.put("shop_id", storeId);
     //   param.put("item_id", data.getId());
    //    param.put("quantity", quantity);

        Log.e("add to cart normal", "param = " + bookingParams.toString());

        Call<ResponseBody> call = api.addToCartApiCall(bookingParams);
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
                        startActivity(new Intent(setDeliveryNormailAct.this,MyCartActivity.class));
                        finish();
                      //  ((ShopItemsAct) mContext).getCartCount();
                    } else if (jsonObject.getString("status").equals("2")) {
                        Log.e("fsdfdsfdsf", "responseString = " + responseString);
                        Log.e("fsdfdsfdsf", "response = " + response);
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
                Log.e("fsdfdsfdsf", "response = " + t.getMessage());
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.two_stores_items_added))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }


}