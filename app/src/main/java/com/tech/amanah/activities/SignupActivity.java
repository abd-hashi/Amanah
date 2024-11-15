package com.tech.amanah.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.tech.amanah.Application.MyApplication;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.InternetConnection;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.databinding.ActivitySignupBinding;
import com.tech.amanah.shops.activities.AddShopDetailsAct;
import com.tech.amanah.taxiservices.models.ModelLogin;
import com.tech.amanah.utility.GPSTracker;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 101;
    ActivitySignupBinding binding;
    Context mContext = SignupActivity.this;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private String registerId = "";
    private LatLng latLng = null;
    GPSTracker gpsTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        sharedPref = SharedPref.getInstance(mContext);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        gpsTracker = new GPSTracker(SignupActivity.this);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                registerId = token;
                Log.e("tokentoken", "retrieve token successful : " + token);
            } else {
                Log.e("tokentoken", "token should not be null...");
            }
        }).addOnFailureListener(e -> {
        }).addOnCanceledListener(() -> {
        }).addOnCompleteListener(task -> Log.e("tokentoken", "This is the token : " + task.getResult()));

        itit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermissions()) {
            gpsTracker = new GPSTracker(SignupActivity.this);
            if (gpsTracker.canGetLocation()) {
                Log.e("asdasdasdas", "Location = " + gpsTracker.getLocation());
            } else {
                gpsTracker.showSettingsAlert();
            }
        } else {
            requestPermissions();
        }
    }

    public void requestPermissions() {
        ActivityCompat.requestPermissions(SignupActivity.this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        }, 101);
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void itit() {

        binding.changeLang.setOnClickListener(v -> {
            changeLangDialog();
        });

        binding.tvLogin.setOnClickListener(v -> {
            Intent i = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        });

        binding.address.setOnClickListener(v -> {
            if (InternetConnection.checkConnection(mContext)) {
                if(gpsTracker.canGetLocation()) {
                    Log.e("asdadasd","Inside CanGetLocation");
                    sharedPref.setScreenType("type","signup");
                    startActivityForResult(new Intent(mContext, PinLocationActivity.class), 222);
                } else {
                    gpsTracker.showSettingsAlert();
                }
            } else {
                MyApplication.showConnectionDialog(mContext);
            }
//            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
//            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                    .build(this);
//            startActivityForResult(intent,AUTOCOMPLETE_REQUEST_CODE);
        });

        binding.btSignUp.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etUsername.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_username), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_name), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_email_add), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etPhone.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_phone_add), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.address.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_add), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.landAddress.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_landmark_address), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.pass.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_pass), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.confirmPass.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_conf_pass), Toast.LENGTH_SHORT).show();
            } else if (!(binding.pass.getText().toString().trim().length() > 4)) {
                Toast.makeText(mContext, getString(R.string.password_validation_text), Toast.LENGTH_SHORT).show();
            } else if (!(binding.pass.getText().toString().trim().equals(binding.confirmPass.getText().toString().trim()))) {
                Toast.makeText(mContext, getString(R.string.password_not_match), Toast.LENGTH_SHORT).show();
            } else if (!ProjectUtil.isValidEmail(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            } else if (!validateUsing_libphonenumber(binding.etPhone.getText().toString().replace(" ", "")
                    , binding.ccp.getSelectedCountryCode())) {
                Toast.makeText(mContext, getString(R.string.invalid_number), Toast.LENGTH_SHORT).show();
            } else {
                HashMap<String, String> params = new HashMap<>();

                params.put("user_name", binding.etUsername.getText().toString().trim());
                params.put("email", binding.etEmail.getText().toString().trim());
                params.put("mobile", binding.etPhone.getText().toString().trim());
                params.put("register_id", registerId);
                params.put("address", binding.address.getText().toString() + " " + binding.landAddress.getText().toString());
                params.put("lat", String.valueOf(latLng.latitude));
                params.put("lon", String.valueOf(latLng.longitude));
                params.put("password", binding.pass.getText().toString().trim());
                params.put("name", binding.etName.getText().toString().trim());

                if (binding.spUserType.getSelectedItemPosition() == 0) {
                    params.put("type", "USER");
                } else {
                    params.put("type", "SHOP");
                }

                if (InternetConnection.checkConnection(mContext)) {
                    signUpApiCall(params);
                } else {
                    MyApplication.showConnectionDialog(mContext);
                }

//              String mobileWithCounCode = (binding.ccp.getSelectedCountryCodeWithPlus()
//              + binding.etPhone.getText().toString().trim()).replace(" ","");

            }
        });

    }

    private void changeLangDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.change_language_dialog);
        dialog.setCancelable(true);

        Button btContinue = dialog.findViewById(R.id.btContinue);
        RadioButton radioEng = dialog.findViewById(R.id.radioEng);
        RadioButton radioSpanish = dialog.findViewById(R.id.radioSpanish);

        if ("so".equals(sharedPref.getLanguage("lan"))) {
            radioSpanish.setChecked(true);
        } else {
            radioEng.setChecked(true);
        }

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        btContinue.setOnClickListener(v -> {
            if (radioEng.isChecked()) {
                ProjectUtil.updateResources(mContext, "en");
                sharedPref.setlanguage("lan", "en");
                finish();
                startActivity(new Intent(mContext, SignupActivity.class));
                dialog.dismiss();
            } else if (radioSpanish.isChecked()) {
                ProjectUtil.updateResources(mContext, "so");
                sharedPref.setlanguage("lan", "so");
                finish();
                startActivity(new Intent(mContext, SignupActivity.class));
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void signUpApiCall(HashMap<String, String> paramHash) {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.signUpApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);

                        modelLogin.getResult().setShop_status("0");

                        sharedPref.setBooleanValue(AppConstant.IS_REGISTER, true);
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                        if (binding.spUserType.getSelectedItemPosition() == 0) {
                            startActivity(new Intent(mContext, SelectService.class));
                            finish();
                        } else {
                            startActivity(new Intent(mContext, AddShopDetailsAct.class));
                            finish();
                        }

                    } else {
                        Toast.makeText(SignupActivity.this, jsonObject.getString("result"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exceptjkion", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });


    }

    private boolean validateUsing_libphonenumber(String phNumber, String code) {

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(code));
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode);
        } catch (Exception e) {
            System.err.println(e);
        }

        boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
        if (isValid) {
            String internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            // Toast.makeText(this, "Phone Number is Valid " + internationalFormat, Toast.LENGTH_LONG).show();
            return true;
        } else {
            // Toast.makeText(this, "Phone Number is Invalid " + phoneNumber, Toast.LENGTH_LONG).show();
            return false;
        }

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
            binding.address.setText(add);
        }
//        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                Place place = Autocomplete.getPlaceFromIntent(data);
//                latLng = place.getLatLng();
//                try {
//                    String addresses = ProjectUtil.getCompleteAddressString(mContext,place.getLatLng().latitude, place.getLatLng().longitude);
//                    binding.address.setText(addresses);
//                } catch (Exception e) {}
//            }
//
//        }

    }

}