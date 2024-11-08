package com.tech.amanah.taxiservices.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.databinding.ActivityPaymentTaxiBinding;
import com.tech.amanah.taxiservices.Dialogs.DialogMessage;
import com.tech.amanah.taxiservices.ModelCurrentBooking;
import com.tech.amanah.taxiservices.models.ModelCurrentBookingResult;
import com.tech.amanah.taxiservices.models.ModelLogin;
import com.tech.amanah.taxiservices.models.ModelTaxiPayment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentTaxiAct extends AppCompatActivity {

    Context mContext = PaymentTaxiAct.this;
    ActivityPaymentTaxiBinding binding;
    private ModelCurrentBooking data;
    ModelCurrentBookingResult bookingDetail;
    private ModelTaxiPayment result;
    String type = "", resquestId = "";
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_taxi);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        resquestId = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");

        try {
            if (getIntent() != null) {

                if ("home".equals(type)) {
                    data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
                    bookingDetail = data.getResult().get(0);
                    Log.e("resultresult", "Result bookingDetail = " + new Gson().toJson(bookingDetail));
                } else {
                    result = (ModelTaxiPayment) getIntent().getSerializableExtra("data");
                    Log.e("resultresult", "Result = Payment" + new Gson().toJson(result));
                }

            }
        } catch (Exception e) {
        }

        itit();

    }

    private void itit() {

        binding.btnPay.setOnClickListener(v -> {
          //  paymentConiformDialog();
            startActivity(new Intent(mContext, RateAct.class)
                    .putExtra("from","taxi")
                    .putExtra("driver_id",bookingDetail.getDriverId())
                    .putExtra("request_id",bookingDetail.getId())
                    .putExtra("driver_name",bookingDetail.getUserDetails().get(0).getName())
                    .putExtra("driver_image",bookingDetail.getDriverId()));
            finish();
        });

        if ("home".equals(type)) {
            Log.e("bookingDetail", "pick = " + bookingDetail.getPicuplocation());
            Log.e("bookingDetail", "drop = " + bookingDetail.getDropofflocation());
            Log.e("bookingDetail", "pay = " + bookingDetail.getSharerideType());
            Log.e("bookingDetail", "amount = " + bookingDetail.getAmount());
            Log.e("bookingDetail", "amount = " + bookingDetail.getDistance());
            Log.e("bookingDetail", "waiting = " + bookingDetail.getWaitingTimeMinute());
            Log.e("bookingDetail", "waiting = " + bookingDetail.getWaitingTimeCharge()+"");
            Log.e("bookingDetail", "distance = " + bookingDetail.getAmount());
            binding.tvPickUpLoc.setText(bookingDetail.getPicuplocation());
            binding.tvDropUpLoc.setText(bookingDetail.getDropofflocation());
            binding.tvPayType.setText(bookingDetail.getSharerideType());
            binding.tvTotalPay.setText(bookingDetail.getAmount() + " Birr");
            binding.tvDistance.setText(bookingDetail.getDistance() + " Km");
            binding.tvWaiting.setText(bookingDetail.getWaitingTimeMinute() + " Min");
            binding.tvIncentive.setText(bookingDetail.getIncentive_amount() + " Birr");
            binding.tvWaitAmount.setText(bookingDetail.getWaitingTimeCharge() + " Birr");
            binding.tvTotalRideCost.setText(bookingDetail.getTotal_amount() + " Birr");
        } else {
            Log.e("bookingDetail", "tvWaiting = " + result.getResult().getWaiting_time());
            binding.tvPickUpLoc.setText(result.getResult().getPicuplocation());
            binding.tvDropUpLoc.setText(result.getResult().getDropofflocation());
            binding.tvPayType.setText(result.getResult().getShareride_type());
            binding.tvDistance.setText(result.getResult().getDistance() + " Km");
            binding.tvWaiting.setText(result.getResult().getWaiting_time() + " Min");
            binding.tvTotalPay.setText( result.getResult().getAmount());
             binding.tvWaitAmount.setText( result.getResult().getAmount() + " Birr");
        }

    }

 /*   private void paymentConiformDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure you get the payment from customer ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                DriverChangeStatus("Finish");
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void DriverChangeStatus(String status) {

        HashMap<String, String> params = new HashMap<>();
        params.put("request_id", resquestId);
        params.put("status", status);
        params.put("cancel_reason", "");
        params.put("timezone", TimeZone.getDefault().getID());
        params.put("driver_id", modelLogin.getResult().getId());

        Log.e("sdfsdfsdfdsfdsfdsfds", "params = " + params);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.acceptCancelOrderCallTaxi(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResp = response.body().string();
                    Log.e("zsdfasdfasdfas", "stringResp = " + stringResp);
                    // Log.e("zsdfasdfasdfas","modelTaxiPayment = " + new Gson().toJson(modelTaxiPayment));
                    JSONObject object = new JSONObject(stringResp);
                    if (object.getString("status").equals("1")) {
                        finish();
                        startActivity(new Intent(mContext, TaxiHomeAct.class));
                    } else {
                        DialogMessage.get(mContext).setMessage("Something went wrong! try again").show();
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

    }*/

}