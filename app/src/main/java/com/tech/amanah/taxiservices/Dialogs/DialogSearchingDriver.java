package com.tech.amanah.taxiservices.Dialogs;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tech.amanah.Constent.BaseClass;
import com.tech.amanah.Constent.Config;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.SessionManager;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.databinding.DialogSearchDriverBinding;
import com.tech.amanah.taxiservices.Interfaces.onSearchingDialogListener;
import com.tech.amanah.taxiservices.ModelCurrentBooking;
import com.tech.amanah.taxiservices.models.ModelCurrentBookingResult;
import com.tech.amanah.taxiservices.models.ModelLogin;
import com.tech.amanah.utility.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;

public class DialogSearchingDriver extends Dialog {

    public  DialogSearchDriverBinding binding;
     onSearchingDialogListener listener;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private CountDownTimer timer;
    Dialog dialog;
    Context context;

    public static DialogSearchingDriver get(Context context) {
        return new DialogSearchingDriver(context);
    }

    public DialogSearchingDriver(Context context) {
        super(context);
        this.context = context;
    }

    public DialogSearchingDriver Callback(onSearchingDialogListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      // dialog = new Dialog(context, WindowManager.LayoutParams.MATCH_PARENT);
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_search_driver, null, false);
        setContentView(binding.getRoot());
        sharedPref = SharedPref.getInstance(getContext());
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setCanceledOnTouchOutside(true);
        binding.btnCancel.setOnClickListener(v -> onBackPressed());
        getStart(1, (onSearchingDialogListener) context);


    }

    public void getStart(int i,onSearchingDialogListener listener) {
          this.listener = listener;
       if(i==1) {
           binding.ripple.startRippleAnimation();
          /* timer = new CountDownTimer(1000, 50000) {
               @Override
               public void onTick(long millisUntilFinished) {
                  // getCurrentBooking();
                  // listener.onRequestAccepted111();
               }

               @Override
               public void onFinish() {
                   timer.start();
               }
           }.start();*/
       }
       else if(i==2) {
         //  if(timer!=null)
         //  timer.cancel();
         //  binding.ripple.stopRippleAnimation();

           //if (dialog!=null) dialog.dismiss();
           listener.onRequestCancel11();
           dismiss();
       }
       else dismiss();

    }

    @Override
    protected void onStart() {
        super.onStart();
     //   LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver,
       //         new IntentFilter("Job_Status_Action"));


    }

    @Override
    protected void onStop() {
        super.onStop();

       // LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        DialogMessage.get(getContext()).setMessage("Are you sure want to cancel Request?")
                .Callback(this::CancelRide).show();
    }


    private void CancelRide() {
        HashMap<String, String> parmas = new HashMap<>();
        parmas.put("request_id", sharedPref.getLanguage(AppConstant.LAST));
        ApiCallBuilder.build(getContext()).setUrl(BaseClass.get().cancelRide())
                .isShowProgressBar(true)
                .setParam(parmas).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    boolean status = object.getString("status").contains("1");
                    Toast.makeText(getContext(), "" + object.getString("message"), Toast.LENGTH_SHORT).show();
                    if (status) {
                        listener.onRequestCancel();
                        dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void Failed(String error) {

            }
        });
    }

   /* BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("Job_Status_Action")) {
                String message = intent.getStringExtra("message");
                JSONObject data = null;
                try {
                    data = new JSONObject(message);
                    String keyMessage = data.getString("key").trim();
                    Log.e("Notification_key", "" + keyMessage);
                    String request_id = data.getString("request_id");
                    sharedPref.setlanguage(AppConstant.LAST, request_id);
                    if (keyMessage.equalsIgnoreCase("your booking request is Accept")) {
                        String driver_id = data.getString("driver_id");
                        getCurrentBooking();
                        timer.cancel();
                        dismiss();
                    }
                    if (keyMessage.equalsIgnoreCase("your booking request is Cancel")) {
                        listener.onRequestCancel();
                        timer.cancel();
                        dismiss();
                    }
                    if (keyMessage.equalsIgnoreCase("no driver available")) {
                        listener.onDriverNotFound();
                        timer.cancel();
                        dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void getCurrentBooking() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("type", "USER");
        param.put("timezone", Tools.get().getTimeZone());
        ApiCallBuilder.build(getContext())
                .setUrl(BaseClass.get().getCurrentBooking())
                .setParam(param).isShowProgressBar(false)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        try {

                            Log.e("sgfgsdfdsgdf","responseresponse = " + response);

                            JSONObject object = new JSONObject(response);
                            if (object.getString("status").equals("1")) {
                                Type listType = new TypeToken<ModelCurrentBooking>() {
                                }.getType();
                                ModelCurrentBooking data = new GsonBuilder().create().fromJson(response, listType);
                                if (data.getStatus().equals(1)) {
                                    ModelCurrentBookingResult result = data.getResult().get(0);
                                    if (result.getStatus().equalsIgnoreCase("Pending")) {
                                    } else if (result.getStatus().equalsIgnoreCase("Accept")) {
                                        listener.onRequestAccepted(data);
                                        timer.cancel();
                                        dismiss();
                                    }

                                    else if (result.getStatus().equalsIgnoreCase("Cancel")) {
                                        listener.onRequestCancel();
                                        timer.cancel();
                                        dismiss();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void Failed(String error) {
                    }

                });
    }*/

}
