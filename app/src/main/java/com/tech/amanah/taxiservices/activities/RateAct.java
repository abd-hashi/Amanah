package com.tech.amanah.taxiservices.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.activities.SelectService;
import com.tech.amanah.databinding.ActivityRateBinding;
import com.tech.amanah.taxiservices.models.ModelLogin;
import com.tech.amanah.utility.NetworkAvailablity;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RateAct extends AppCompatActivity {
    ActivityRateBinding binding;
    String driverId="",requestId="",driverName="",driverImage="",userId="",from="";
    Api apiInterface;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_rate);
        apiInterface = ApiFactory.getClientWithoutHeader(RateAct.this).create(Api.class);
        sharedPref = SharedPref.getInstance(RateAct.this);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        initViews();
    }

    private void initViews() {
      if(getIntent()!=null){
          from = getIntent().getStringExtra("from");
          driverId = getIntent().getStringExtra("driver_id");
          requestId = getIntent().getStringExtra("request_id");
          driverName = getIntent().getStringExtra("driver_name");
          driverImage = getIntent().getStringExtra("driver_image");

          binding.tvDrivername.setText(driverName);
          Glide.with(RateAct.this).load(driverImage).error(R.drawable.user_ic).into(binding.driveUserProfile);
      }


      binding.btSubmit.setOnClickListener(v -> {
          if(NetworkAvailablity.getInstance(RateAct.this).checkNetworkStatus()) addFeedbackApi(String.valueOf(binding.ratingBar.getRating()),binding.tvComment.getText().toString());
           else Toast.makeText(this, getString(R.string.please_check_internet), Toast.LENGTH_SHORT).show();
      });

    }

    private void addFeedbackApi(String rating, String comment) {
        ProjectUtil.showProgressDialog(RateAct.this, false, getString(R.string.please_wait));
        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",modelLogin.getResult().getId());
        params.put("rating",rating);
        params.put("request_id",requestId);
        params.put("review",comment);
        params.put("driver_id",driverId);
        if(from.equalsIgnoreCase("taxi"))params.put("type","taxi");
        else params.put("type","shop");

        Call<ResponseBody> call = apiInterface.addFeedbackByUserApiCall(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        Toast.makeText(RateAct.this, "Rated successfully", Toast.LENGTH_SHORT).show();
                       if (from.equalsIgnoreCase("taxi")){
                           startActivity(new Intent(RateAct.this,TaxiHomeAct.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                           finish();
                       }
                       else {
                           startActivity(new Intent(RateAct.this, SelectService.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                           finish();
                       }

                        // Log.e("responseStringdfsdfsd","responseString = " + responseString);

                    } else {
                        Toast.makeText(RateAct.this,jsonObject.getString("message") , Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }



}
