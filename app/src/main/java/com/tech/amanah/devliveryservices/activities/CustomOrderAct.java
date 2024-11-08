package com.tech.amanah.devliveryservices.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.activities.PinLocationActivity;
import com.tech.amanah.databinding.ActivityCustomOrderBinding;
import com.tech.amanah.devliveryservices.models.VehicleModel;
import com.tech.amanah.taxiservices.models.ModelLogin;
import com.tech.amanah.utility.Tools;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomOrderAct extends AppCompatActivity {
    ActivityCustomOrderBinding binding;
    private LatLng PickUpLatLng, DropOffLatLng;
    ModelLogin modelLogin;
    SharedPref sharedPref;
    String customId="",vehicleId="";
    ArrayList<VehicleModel.Result> vehicleArrayList;
    public static String typeAddress = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_custom_order);
        sharedPref = SharedPref.getInstance(CustomOrderAct.this);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        initViews();
    }

    private void initViews() {


        binding.ivBack.setOnClickListener(v -> finish());

        vehicleArrayList = new ArrayList<>();

        if(getIntent()!=null) customId = getIntent().getStringExtra("custom_id");
        binding.etItemLocation.setOnClickListener(v -> {
         /*   List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, 1002);*/

            typeAddress = "PickUp";
            sharedPref.setScreenType("type","customOrder");
            startActivityForResult(new Intent(CustomOrderAct.this, PinLocationActivity.class), 222);
        });

        binding.etDropLocation.setOnClickListener(v -> {
          /*  List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, 1003);*/
            typeAddress = "DropOff";
            sharedPref.setScreenType("type","customOrder");
            startActivityForResult(new Intent(CustomOrderAct.this, PinLocationActivity.class), 222);

        });

        binding.btnNext.setOnClickListener(v -> validation());

        getVehicle();

        binding.etVehicle.setOnClickListener(v -> {
            if(vehicleArrayList!=null) showDropDownVehicle(v,binding.etVehicle,vehicleArrayList);
        });

    }


    private void showDropDownVehicle(View v, TextView textView, List<VehicleModel.Result> stringList) {
        PopupMenu popupMenu = new PopupMenu(CustomOrderAct.this, v);
        for (int i = 0; i < stringList.size(); i++) {
            popupMenu.getMenu().add(stringList.get(i).getCarName());
        }
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            textView.setText(menuItem.getTitle());
            for (int i = 0; i < stringList.size(); i++) {
                if(stringList.get(i).getCarName().equalsIgnoreCase(menuItem.getTitle().toString())) {
                    vehicleId = stringList.get(i).getId();

                }
            }
            return true;
        });
        popupMenu.show();
    }




    private void validation() {
        if(binding.etItemName.getText().toString().equalsIgnoreCase(""))
            Toast.makeText(this, getString(R.string.please_enter_item_name), Toast.LENGTH_SHORT).show();

        else if(binding.etItemLocation.getText().toString().equalsIgnoreCase(""))
            Toast.makeText(this, getString(R.string.please_enter_item_location), Toast.LENGTH_SHORT).show();

        else if(binding.etLandmark.getText().toString().equalsIgnoreCase(""))
            Toast.makeText(this, getString(R.string.please_enter_landmak), Toast.LENGTH_SHORT).show();

        else if(binding.etName.getText().toString().equalsIgnoreCase(""))
            Toast.makeText(this, getString(R.string.please_enter_name), Toast.LENGTH_SHORT).show();

        else if(binding.etMobileNumber.getText().toString().equalsIgnoreCase(""))
            Toast.makeText(this, getString(R.string.please_enter_mobile_number), Toast.LENGTH_SHORT).show();

       /* else  if(binding.etDropLocation.getText().toString().equalsIgnoreCase(""))
            Toast.makeText(this, getString(R.string.please_enter_drop_off_location), Toast.LENGTH_SHORT).show();

        else  if(binding.etDropLandmark.getText().toString().equalsIgnoreCase(""))
            Toast.makeText(this, getString(R.string.please_enter_drop_off_landmark), Toast.LENGTH_SHORT).show();*/

        else  if(vehicleId.equalsIgnoreCase(""))
            Toast.makeText(this, getString(R.string.please_select_type), Toast.LENGTH_SHORT).show();

       else {
          // addCustomOrder();
           startActivity(new Intent(CustomOrderAct.this,SetDeliveryAct.class)
                   .putExtra("category_id",customId)
                   .putExtra("item_name",binding.etItemName.getText().toString())
                   .putExtra("from_address",binding.etItemLocation.getText().toString())
                   .putExtra("item_landmark",binding.etLandmark.getText().toString())
                   .putExtra("from_lat",PickUpLatLng.latitude+"")
                   .putExtra("from_lon",PickUpLatLng.longitude+"")
                   .putExtra("name",binding.etName.getText().toString())
                   .putExtra("mobile",binding.etMobileNumber.getText().toString())
                   .putExtra("vehicle",vehicleId)

           );
        }


    }

    private void addCustomOrder() {
        ProjectUtil.showProgressDialog(CustomOrderAct.this, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(CustomOrderAct.this).create(Api.class);


        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("category_id", customId);
        param.put("item_name", binding.etItemName.getText().toString());
        param.put("from_address",binding.etItemLocation.getText().toString() );
        param.put("item_landmark",binding.etLandmark.getText().toString() );
        param.put("from_lat", PickUpLatLng.latitude+"");
        param.put("from_lon", PickUpLatLng.longitude+"");
        param.put("to_address",binding.etDropLocation.getText().toString() );
        param.put("drop_landmark",binding.etDropLandmark.getText().toString() );
        param.put("to_lat", DropOffLatLng.latitude+"");
        param.put("to_lon", DropOffLatLng.longitude+"");
        param.put("name", binding.etName.getText().toString() );
        param.put("mobile",binding.etMobileNumber.getText().toString() );
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

                    startActivity(new Intent(CustomOrderAct.this,MyCartActivity.class));
                    finish();
                    } else if (jsonObject.getString("status").equals("2")) {
                        Log.e("fsdfdsfdsf", "responseString = " + responseString);
                        Log.e("fsdfdsfdsf", "response = " + response);
                    } else if (jsonObject.getString("status").equals("3")) {
                    }
                } catch (Exception e) {
                    Toast.makeText(CustomOrderAct.this, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
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


    private void getVehicle() {
        Api api = ApiFactory.getClientWithoutHeader(CustomOrderAct.this).create(Api.class);
        Call<ResponseBody> call = api.getAllVehicleApiCall();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("getAllVehicle", "responseString = " + responseString);
                    if (jsonObject.getString("status").equals("1")) {
                        VehicleModel vehicleModel = new Gson().fromJson(responseString,VehicleModel.class);
                        vehicleArrayList.clear();
                        vehicleArrayList.addAll(vehicleModel.getResult());

                    } else if (jsonObject.getString("status").equals("2")) {
                        Log.e("fsdfdsfdsf", "responseString = " + responseString);
                        Log.e("fsdfdsfdsf", "response = " + response);
                    } else if (jsonObject.getString("status").equals("3")) {
                    }
                } catch (Exception e) {
                    Toast.makeText(CustomOrderAct.this, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
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




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if (requestCode == 1002) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                PickUpLatLng = place.getLatLng();
                binding.etItemLocation.setText(Tools.getCompleteAddressString(CustomOrderAct.this, PickUpLatLng.latitude, PickUpLatLng.longitude));

            }
        } else if (requestCode == 1003) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                DropOffLatLng = place.getLatLng();
                binding.etDropLocation.setText(Tools.getCompleteAddressString(CustomOrderAct.this, DropOffLatLng.latitude, DropOffLatLng.longitude));

            }
        }*/

        if (resultCode == 222) {
            String add = data.getStringExtra("add");
            Log.e("sfasfdas", "fdasfdas = 222 = " + add);
            Log.e("sfasfdas", "fdasfdas = lat = " + data.getDoubleExtra("lat", 0.0));
            Log.e("sfasfdas", "fdasfdas = lon = " + data.getDoubleExtra("lon", 0.0));
            double lat = data.getDoubleExtra("lat", 0.0);
            double lon = data.getDoubleExtra("lon", 0.0);
            if(typeAddress.equalsIgnoreCase("PickUp")){
                PickUpLatLng = new LatLng(lat, lon);
                binding.etItemLocation.setText(add);

            }

            else if(typeAddress.equalsIgnoreCase("DropOff")){
                DropOffLatLng = new LatLng(lat, lon);
                binding.etDropLocation.setText(add);

            }
        }


    }


}
