package com.tech.amanah.taxiservices.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.tntkhang.fullscreenimageview.library.FullScreenImageViewActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.tech.amanah.Constent.BaseClass;
import com.tech.amanah.Constent.Config;
import com.tech.amanah.Constent.DrawPollyLine;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.DataParser2;
import com.tech.amanah.Utils.GPSTracker;
import com.tech.amanah.Utils.LatLngInterpolator;
import com.tech.amanah.Utils.MarkerAnimation;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SessionManager;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.databinding.ActivityTrackBinding;
import com.tech.amanah.databinding.RideCancellationDialogBinding;
import com.tech.amanah.taxiservices.Dialogs.DialogMessage;
import com.tech.amanah.taxiservices.ModelCurrentBooking;
import com.tech.amanah.taxiservices.models.ModelCurrentBookingResult;
import com.tech.amanah.taxiservices.models.ModelLogin;
import com.tech.amanah.taxiservices.models.UserDetail;
import com.tech.amanah.utility.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.develpoeramit.mapicall.ApiCallBuilder;

public class TrackActivity extends AppCompatActivity implements OnMapReadyCallback {

    Context mContext = TrackActivity.this;
    ActivityTrackBinding binding;
    private ModelCurrentBooking data;
    private ModelCurrentBookingResult result;
    private ModelLogin.Result DriverDetails;
    private LatLng PicLatLon, DropLatLon;
    private GoogleMap mMap;
    private MarkerOptions DriverMarker;
    private MarkerOptions DropOffMarker;
    private SessionManager session;
    private Marker driverMarkerCar;
    Timer timer = null;
    String driverId = "", usermobile = "",distance="";
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private Animator currentAnimator;
    private int shortAnimationDuration;
    ArrayList<String> uriString = new ArrayList<>();
    GPSTracker gpsTracker;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_track);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        gpsTracker = new GPSTracker(TrackActivity.this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initView();
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getDriverLocation();
            }
        }, 0, 4000);

        if (getIntent() != null) {
            data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
            setUserData(data);
        }

        binding.driverImage.setOnClickListener(v -> {
            if (DriverDetails.getProfile_image() != null)
                uriString.add(DriverDetails.getProfile_image());

                Intent fullImageIntent = new Intent(TrackActivity.this, FullScreenImageViewActivity.class);
// uriString is an ArrayList<String> of URI of all images
               fullImageIntent.putExtra(FullScreenImageViewActivity.URI_LIST_DATA, uriString);
// pos is the position of image will be showned when open
              fullImageIntent.putExtra(FullScreenImageViewActivity.IMAGE_FULL_SCREEN_CURRENT_POS, 0);
            startActivity(fullImageIntent);
             //  zoomImageFromThumb(thumb1View,R.drawable.user_ic,DriverDetails.getProfile_image());
        });

    }

    private void setUserData(ModelCurrentBooking data) {
        result = data.getResult().get(0);
        driverId = result.getDriverId();
        DriverDetails = result.getUserDetails().get(0);
        usermobile = DriverDetails.getMobile();
        binding.tvVehicleType.setText(getIntent().getStringExtra("carName"));
       // binding.tvFare.setText(getIntent().getStringExtra("fare") + " Birr");
        if(result.getStatus().equalsIgnoreCase("Arrived") || result.getStatus().equalsIgnoreCase("Start"))
        {
            binding.tvFare.setVisibility(View.VISIBLE);
            binding.tvFare.setText(result.getTotal_amount() + " Birr");
        }else binding.tvFare.setVisibility(View.GONE);

        PicLatLon = new LatLng(Double.parseDouble(result.getPicuplat()), Double.parseDouble(result.getPickuplon()));

        try {
            DropLatLon = new LatLng(Double.parseDouble(result.getDroplat()), Double.parseDouble(result.getDroplon()));
        } catch (Exception e) {}

        binding.setDriver(DriverDetails);
        if (DriverDetails.getProfile_image() != null) {
            Glide.with(mContext).load(DriverDetails.getProfile_image())
                    .placeholder(R.drawable.user_ic).into(binding.driverImage);
        }
    }

    private void rideCancelDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        RideCancellationDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.ride_cancellation_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btnSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etReason.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_reason), Toast.LENGTH_SHORT).show();
            } else {
                dialog.dismiss();
                CancelRide(dialogBinding.etReason.getText().toString().trim());
            }
        });

        dialog.show();

    }

    private void CancelRide(String reason) {
        HashMap<String, String> parmas = new HashMap<>();
        parmas.put("request_id", sharedPref.getLanguage(AppConstant.LAST));
        parmas.put("cancel_reason", reason);
        Log.e("asdasdffffffff", sharedPref.getLanguage(AppConstant.LAST));
        Log.e("asdasdffffffff", BaseClass.get().cancelRide() + "?request_id=" + sharedPref.getLanguage(AppConstant.LAST));
        ApiCallBuilder.build(TrackActivity.this)
                .setUrl(BaseClass.get().cancelRide())
                .isShowProgressBar(true)
                .setParam(parmas).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    boolean status = object.getString("status").contains("1");
                    Toast.makeText(TrackActivity.this, "" + object.getString("message"), Toast.LENGTH_SHORT).show();
                    if (status) {
                        finishAffinity();
                        startActivity(new Intent(TrackActivity.this, TaxiHomeAct.class));
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

    protected void zoomMapInitial(LatLng finalPlace, LatLng currenLoc) {
        try {
            int padding = 200;
            LatLngBounds.Builder bc = new LatLngBounds.Builder();
            bc.include(finalPlace);
            bc.include(currenLoc);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), padding));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {

        DropOffMarker = new MarkerOptions()
                .title("Drop Off Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.icCall.setOnClickListener(v -> {
            ProjectUtil.call(mContext, usermobile);
        });

        binding.btnRate.setOnClickListener(v -> {
            startActivity(new Intent(TrackActivity.this, TaxiHomeAct.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });

        binding.ivCancel.setOnClickListener(v -> {
            rideCancelDialog();
//            DialogMessage.get(TrackActivity.this)
//                    .setMessage(getString(R.string.cancel_ride_text))
//                    .Callback(() -> {
//                    }).show();
        });


        binding.icChat.setOnClickListener(v -> {
            if(data!=null){
                startActivity(new Intent(TrackActivity.this,TaxiChatingActivity.class)
                        .putExtra("request_id",data.getResult().get(0).getId())
                        .putExtra("receiver_id",data.getResult().get(0).getDriverId())
                        .putExtra("name",data.getResult().get(0).getUserDetails().get(0).getName())
                        .putExtra("sender_id",modelLogin.getResult().getId()));
                       // .putExtra("driver_image",data.getResult().get(0).getUserDetails().get(0).getProfile_image()));
            }
        });

        binding.btnMoving.setOnClickListener(v -> {
            if (gpsTracker!=null) getKm();
        });




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        // drawRoute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("editTextValue");
                binding.titler.setText("Send Feedback");
                binding.btnBack.setVisibility(View.GONE);
                binding.rlDriver.setVisibility(View.GONE);
                binding.rlFeedback.setVisibility(View.VISIBLE);
            }
        }
    }

    BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("gdsfdsfdsf", "statusReceiver");
            if (intent.getAction().equals("Job_Status_Action")) {
                if (intent.getStringExtra("status").equals("Cancel")) {
                    finish();
                    Toast.makeText(mContext, "Your ride has been cancelled by driver", Toast.LENGTH_SHORT).show();
                }

               else if (intent.getStringExtra("status").equals("admin_cancel")) {
                    Toast.makeText(mContext, "Your ride has been cancelled by admin", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TrackActivity.this, TaxiHomeAct.class));
                    finish();
                }
                else if (intent.getStringExtra("status").equals("New Driver Assign")) {
                    getCurrentBooking();
                }
                else if (intent.getStringExtra("status").equals("New Driver Booking")) {
                    getCurrentBooking();
                }




                else if (intent.getStringExtra("status").equals("Chat")) {
                    GetCounter();
                }

                else {
                    getCurrentBooking();
                }
            }
        }
    };

    BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
            } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                String message = intent.getStringExtra("message");
                JSONObject data = null;
                try {
                    data = new JSONObject(message);
                    String keyMessage = data.getString("key").trim();
                    Log.e("KEY ACCEPT REJ", "" + keyMessage);
                    getCurrentBooking();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    private void GetCounter() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Log.e("counterChat", "param = " + param);
        Call<ResponseBody> call = api.getCounterChat(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String stringResponse = response.body().string();
                    JSONObject object = new JSONObject(stringResponse);
                    Log.e("counterChat", "param = " + stringResponse);
                    if (object.getString("status").equals("1")) {
                        binding.tvCount.setVisibility(View.VISIBLE);
                        binding.tvCount.setText(object.getString("result"));
                    }
                    else if (object.getString("status").equals("0")) {
                        binding.tvCount.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }

        });
    }




    private void drawRoute() {

        DriverMarker.position(PicLatLon);
        DropOffMarker.position(DropLatLon);
        mMap.addMarker(DriverMarker);
        mMap.addMarker(DropOffMarker);

        zoomMapInitial(DropLatLon, PicLatLon);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(PicLatLon));

        DrawPollyLine.get(this)
                .setOrigin(PicLatLon)
                .setDestination(DropLatLon)
                .execute(new DrawPollyLine.onPolyLineResponse() {
                    @Override
                    public void Success(ArrayList<LatLng> latLngs) {
                        PolylineOptions options = new PolylineOptions();
                        options.addAll(latLngs);
                        options.color(Color.BLUE);
                        options.width(10);
                        options.startCap(new SquareCap());
                        options.endCap(new SquareCap());
                        Polyline line = mMap.addPolyline(options);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(statusReceiver, new IntentFilter("Job_Status_Action"));

//      registerReceiver(mRegistrationBroadcastReceiver,new IntentFilter("getstatus"));
//      LocalBroadcastManager.getInstance(TrackActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(Config.PUSH_NOTIFICATION));

        if (timer != null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    getDriverLocation();
                }
            }, 0, 4000);
        } else {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    getDriverLocation();
                }
            }, 0, 4000);
        }

        getCurrentBooking();
        GetCounter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(statusReceiver);
        // unregisterReceiver(mRegistrationBroadcastReceiver);
        if (timer != null) timer.cancel();
//      LocalBroadcastManager.getInstance(TrackActivity.this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(timer != null) timer.cancel();
//    }

    private void getDriverLocation() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", driverId);
        ApiCallBuilder.build(this).setUrl(BaseClass.get().getDriverLatLon())
                .setParam(param).isShowProgressBar(false)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("getDriverLocation", "getDriverLocation = " + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getString("status").equals("1")) {
                                double lat = Double.parseDouble(object.getString("lat"));
                                double lon = Double.parseDouble(object.getString("lon"));
                                Location location = new Location("");
                                location.setLatitude(lat);
                                location.setLongitude(lon);
                                location.getBearing();
                                location.getAccuracy();
                                if (driverMarkerCar == null) {
                                    DriverMarker = new MarkerOptions().title("Driver Here")
                                            .position(new LatLng(lat, lon))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top));
                                    driverMarkerCar = mMap.addMarker(DriverMarker);
                                    zoomCameraToLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                                } else {
                                    driverMarkerCar.setRotation(location.getBearing());
                                    Log.e("LatlonDriver = ", " driver Location = " + new LatLng(lat, lon));
                                    Log.e("LatlonDriver = ", " driver Marker = " + driverMarkerCar);
                                    MarkerAnimation.animateMarkerToGB(driverMarkerCar, new LatLng(location.getLatitude(), location.getLongitude()), new LatLngInterpolator.Spherical());
                                    // zoomCameraToLocation(new LatLng(location.getLatitude(),location.getLongitude()));
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void Failed(String error) {
                    }

                });

    }

    private void zoomCameraToLocation(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }

    private void getCurrentBooking() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("type", "USER");
        param.put("timezone", Tools.get().getTimeZone());
        ApiCallBuilder.build(this).setUrl(BaseClass.get().getCurrentBooking())
                .setParam(param).isShowProgressBar(false)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getString("status").equals("1")) {
                                Type listType = new TypeToken<ModelCurrentBooking>() {
                                }.getType();
                              //  ModelCurrentBooking data = new GsonBuilder().create().fromJson(response, listType);
                                 data = new GsonBuilder().create().fromJson(response, listType);

                                if (data.getStatus().equals(1)) {
                                    ModelCurrentBookingResult result = data.getResult().get(0);
                                    setUserData(data);
                                    if (result.getStatus().equalsIgnoreCase("Pending")) {
                                        binding.btnMoving.setVisibility(View.GONE);
                                    } else if (result.getStatus().equalsIgnoreCase("Accept")) {
                                        binding.btnMoving.setVisibility(View.GONE);
                                    } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                        binding.btnMoving.setVisibility(View.GONE);
                                        DialogMessage.get(TrackActivity.this).setMessage(getString(R.string.driver_arrived)).show();
                                    } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                        binding.btnMoving.setVisibility(View.VISIBLE);
                                        DialogMessage.get(TrackActivity.this).setMessage(getString(R.string.trip_start)).show();
                                    } else if (result.getStatus().equalsIgnoreCase("End")) {
                                      //  DialogMessage.get(TrackActivity.this).setMessage(getString(R.string.trip_end_text))
                                         //       .Callback(() -> finish()).show();
                                        binding.btnMoving.setVisibility(View.GONE);

                                        Intent j = new Intent(mContext, PaymentTaxiAct.class);
                                        j.putExtra("data", data);
                                        j.putExtra("type", "home");
                                        j.putExtra("id", result.getId());
                                        startActivity(j);
                                        finish();

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

    }


    private void zoomImageFromThumb(final View thumbView, int imageResId,String driverImag) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = binding.expandImage;
       // expandedImageView.setImageResource(imageResId);
        Glide.with(mContext).load(driverImag).into(expandedImageView);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                                .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }

    public void getKm(){
        String URL= BaseClass.get().getPolyLineUrl(TrackActivity.this,new LatLng(PicLatLon.latitude,PicLatLon.longitude),new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude()));
        ApiCallBuilder.build(TrackActivity.this)
                .setUrl(URL).execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        try {
                            JSONObject object=new JSONObject(response);
                            DataParser2 parser = new DataParser2();
                            distance  = parser.parse2(object);
                            Log.e("Distance===",distance);
                            binding.btnMoving.setText(distance   + " completed");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void Failed(String error) {

                    }
                });
    }


}