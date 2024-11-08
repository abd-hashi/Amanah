package com.tech.amanah.shops.activities;

import android.content.Intent;
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
import com.tech.amanah.databinding.ActivityAddInfoBinding;
import com.tech.amanah.devliveryservices.activities.CustomOrderAct;
import com.tech.amanah.devliveryservices.activities.MyCartActivity;
import com.tech.amanah.devliveryservices.activities.SetDeliveryAct;
import com.tech.amanah.taxiservices.models.ModelLogin;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAccountInfoAct extends AppCompatActivity {
    ActivityAddInfoBinding binding;
    ModelLogin modelLogin;
    SharedPref sharedPref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = DataBindingUtil.setContentView(this,R.layout.activity_add_info);
        sharedPref = SharedPref.getInstance(AddAccountInfoAct.this);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        initViews();
    }

    private void initViews() {
        binding.ivBack.setOnClickListener(v -> finish());

        binding.btnNext.setOnClickListener(v -> validation());
    }

    private void validation() {
        if(binding.etHolderName.getText().toString().equals("")){
            binding.etHolderName.setError(getString(R.string.required));
            binding.etHolderName.setFocusable(true);
        }
        else if(binding.etAccountNumber.getText().toString().equals("")){
            binding.etAccountNumber.setError(getString(R.string.required));
            binding.etAccountNumber.setFocusable(true);
        }

        else if(binding.etEb.getText().toString().equals("")){
            binding.etEb.setError(getString(R.string.required));
            binding.etEb.setFocusable(true);
        }

        else if(binding.etSahay.getText().toString().equals("")){
            binding.etSahay.setError(getString(R.string.required));
            binding.etSahay.setFocusable(true);
        }
        else if(binding.etHelloCash.getText().toString().equals("")){
            binding.etHelloCash.setError(getString(R.string.required));
            binding.etHelloCash.setFocusable(true);
        }
        else {
             addBankInfo();
        }
    }

    private void addBankInfo() {
        ProjectUtil.showProgressDialog(AddAccountInfoAct.this, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(AddAccountInfoAct.this).create(Api.class);
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("name", binding.etHolderName.getText().toString());
        param.put("acc_number", binding.etAccountNumber.getText().toString());
        param.put("ebirr",binding.etEb.getText().toString() );
        param.put("sahay",binding.etSahay.getText().toString() );
        param.put("hellocash",binding.etHelloCash.getText().toString() );
        Log.e("Add BankInfo Request", "param = " + param.toString());

        Call<ResponseBody> call = api.addINfoBa(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("Add BankInfo Response", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Toast.makeText(AddAccountInfoAct.this, getString(R.string.added_successfully), Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (jsonObject.getString("status").equals("2")) {
                        Log.e("fsdfdsfdsf", "responseString = " + responseString);
                        Log.e("fsdfdsfdsf", "response = " + response);
                    } else if (jsonObject.getString("status").equals("3")) {
                    }
                } catch (Exception e) {
                    Toast.makeText(AddAccountInfoAct.this, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
