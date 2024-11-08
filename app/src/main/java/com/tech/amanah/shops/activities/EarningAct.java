package com.tech.amanah.shops.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.databinding.ActivityEarningBinding;
import com.tech.amanah.taxiservices.models.ModelLogin;
import com.tech.amanah.utility.NetworkAvailablity;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EarningAct extends AppCompatActivity {
    ActivityEarningBinding binding;
    Api apiInterface;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String from="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_earning);
        apiInterface = ApiFactory.getClientWithoutHeader(EarningAct.this).create(Api.class);

        sharedPref = new SharedPref();
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        initViews();
    }

    private void initViews() {
        if(getIntent()!=null) {
            from = getIntent().getStringExtra("from");
        }

        binding.ivBack.setOnClickListener(v -> finish());

        if(NetworkAvailablity.getInstance(EarningAct.this).checkNetworkStatus()) getAllEarning();
        else Toast.makeText(this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
    }


    private void getAllEarning() {
        ProjectUtil.showProgressDialog(EarningAct.this, false, getString(R.string.please_wait));
        HashMap<String,String> params = new HashMap<>();
        params.put("shop_id",modelLogin.getResult().getShop_id());
        params.put("start_date",ProjectUtil.getCurrentDate2());
        params.put("end_date",ProjectUtil.getCurrentDate2());
   /*     if(from.equalsIgnoreCase("taxi"))params.put("type","taxi");
        else params.put("type","shop");*/
        Log.e("Earning Request","responseString = " + params);
        Call<ResponseBody> call = apiInterface.getEarningShopUser(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e("Earning Response","responseString = " + responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        JSONObject object = jsonObject.getJSONObject("result");
                     //   binding.tvDaily.setText(String.format("%.2f",Double.parseDouble(object.getJSONObject("daily").getString("earning"))) + " " +AppConstant.CURRENCY);
                     //   binding.tvWeekly.setText(String.format("%.2f",Double.parseDouble(object.getJSONObject("week").getString("earning"))) + " " + AppConstant.CURRENCY);
                    //    binding.tvMonthly.setText(String.format("%.2f",Double.parseDouble(object.getJSONObject("month").getString("earning")))+ " " + AppConstant.CURRENCY);
                        binding.tvDaily.setText(object.getJSONObject("daily").getString("earning") + " " +AppConstant.CURRENCY);
                        binding.tvWeekly.setText(object.getJSONObject("week").getString("earning") + " " + AppConstant.CURRENCY);
                        binding.tvMonthly.setText(object.getJSONObject("month").getString("earning")+ " " + AppConstant.CURRENCY);
                    } else if(jsonObject.getString("status").equals("0")){
                        Toast.makeText(EarningAct.this,jsonObject.getString("message") , Toast.LENGTH_SHORT).show();

                    }

                    else if (jsonObject.getString("status").equals("2")) {
                        Toast.makeText(EarningAct.this,"Logout Successfully", Toast.LENGTH_SHORT).show();

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