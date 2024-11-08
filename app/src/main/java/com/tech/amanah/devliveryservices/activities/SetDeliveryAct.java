package com.tech.amanah.devliveryservices.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.model.LatLng;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.activities.PinLocationActivity;
import com.tech.amanah.databinding.ActivityDeliveryBinding;
import com.tech.amanah.databinding.ActivitySetDeliveryLocationBinding;
import com.tech.amanah.taxiservices.models.ModelLogin;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetDeliveryAct extends AppCompatActivity {
    Context mContext = SetDeliveryAct.this;
    ActivityDeliveryBinding binding;
    private LatLng DropOffLatLng;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String customId="",itemName="",fromAddress="",fromLandmark="",fromLat="",fromLon="",name="",mobile="",vehicleId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delivery);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();

    }

    private void itit() {

        if(getIntent()!=null){
            customId = getIntent().getStringExtra("category_id");
            itemName = getIntent().getStringExtra("item_name");
            fromAddress = getIntent().getStringExtra("from_address");
            fromLandmark = getIntent().getStringExtra("item_landmark");
            fromLat = getIntent().getStringExtra("from_lat");
            fromLon = getIntent().getStringExtra("from_lon");
            name = getIntent().getStringExtra("name");
            mobile = getIntent().getStringExtra("mobile");
            vehicleId = getIntent().getStringExtra("vehicle");


        }


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
            else {
              addCustomOrder();

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
            DropOffLatLng = new LatLng(lat, lon);
            binding.etAddress.setText(add);
        }
    }


    private void addCustomOrder() {
        ProjectUtil.showProgressDialog(SetDeliveryAct.this, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(SetDeliveryAct.this).create(Api.class);


        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("category_id", customId);
        param.put("item_name", itemName);
        param.put("from_address",fromAddress );
        param.put("item_landmark",fromLandmark );
        param.put("from_lat",fromLat );
        param.put("from_lon",fromLon );
        param.put("to_address",binding.etAddress.getText().toString() );
        param.put("drop_landmark",binding.etLandMark.getText().toString() );
        param.put("to_lat", DropOffLatLng.latitude+"");
        param.put("to_lon", DropOffLatLng.longitude+"");
        param.put("name",name );
        param.put("mobile",mobile );
        param.put("vehicle",vehicleId);
        param.put("quantity", "1");

        Log.e("CustomOrder", "param = " + param.toString());

        Call<ResponseBody> call = api.customOrderApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("CustomOrder", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        startActivity(new Intent(SetDeliveryAct.this,MyCartActivity.class));
                        finish();
                    } else if (jsonObject.getString("status").equals("2")) {
                        Log.e("fsdfdsfdsf", "responseString = " + responseString);
                        Log.e("fsdfdsfdsf", "response = " + response);
                    } else if (jsonObject.getString("status").equals("3")) {
                    }
                } catch (Exception e) {
                    Toast.makeText(SetDeliveryAct.this, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
