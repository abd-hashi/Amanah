package com.tech.amanah.devliveryservices.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.adapters.NotificationAdapter;
import com.tech.amanah.databinding.ActivityNotificationBinding;
import com.tech.amanah.devliveryservices.models.NotificationModel;
import com.tech.amanah.shops.activities.ShopHomeAct;
import com.tech.amanah.shops.adapters.AdapterShopItems;
import com.tech.amanah.shops.models.ModelShopItems;
import com.tech.amanah.taxiservices.models.ModelLogin;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAct extends AppCompatActivity {
    Context mContext = NotificationAct.this;
    ActivityNotificationBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    ArrayList<NotificationModel.Result>arrayList;
    NotificationAdapter adapterShopItems;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_notification);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        initViews();
    }

    private void initViews() {
        binding.ivBack.setOnClickListener(v -> finish());

        arrayList = new ArrayList<>();

        adapterShopItems = new NotificationAdapter(mContext, arrayList);
        binding.rvNotification.setAdapter(adapterShopItems);

        getNotificationApiCall();

    }


    private void getNotificationApiCall() {
        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getAllNotification(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("getNotificationApiCall", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        binding.tvNotFound.setVisibility(View.GONE);
                        NotificationModel notificationModel = new Gson().fromJson(responseString, NotificationModel.class);
                        arrayList.clear();
                        arrayList.addAll(notificationModel.getResult());
                        adapterShopItems.notifyDataSetChanged();

                    }
                    else {
                        binding.tvNotFound.setVisibility(View.VISIBLE);
                        arrayList.clear();
                        adapterShopItems.notifyDataSetChanged();

                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("Exception", "Throwable = " + t.getMessage());
                binding.tvNotFound.setVisibility(View.VISIBLE);
                arrayList.clear();
                adapterShopItems.notifyDataSetChanged();
            }
        });

    }

}
