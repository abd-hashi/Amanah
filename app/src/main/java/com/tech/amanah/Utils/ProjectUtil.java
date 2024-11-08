package com.tech.amanah.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tech.amanah.BuildConfig;
import com.tech.amanah.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ProjectUtil {

    private static ProgressDialog mProgressDialog;
    private static String registerId = "";

    public static Dialog showProgressDialog(Context context, boolean isCancelable, String message) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
        mProgressDialog.setCancelable(isCancelable);
        return mProgressDialog;
    }

    public static void openGallery(Context mContext, int GALLERY) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        ((Activity) mContext).startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY);
    }

    public static String openCamera(Context mContext, int CAMERA) {

        File dirtostoreFile = new File(Environment.getExternalStorageDirectory() + "/amanah/Images/");

        if (!dirtostoreFile.exists()) {
            dirtostoreFile.mkdirs();
        }

        String timestr = convertDateToString(Calendar.getInstance().getTimeInMillis());

        File tostoreFile = new File(Environment.getExternalStorageDirectory() + "/amanah/Images/" + "IMG_" + timestr + ".jpg");

        String str_image_path = tostoreFile.getPath();

        Uri uriSavedImage = FileProvider.getUriForFile(Objects.requireNonNull(((Activity) mContext)),
                BuildConfig.APPLICATION_ID + ".provider", tostoreFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        ((Activity) mContext).startActivityForResult(intent, CAMERA);

        return str_image_path;

    }


    public static String getFirebaseToken() {

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
        return registerId;
    }


    public static String convertDateToString(long l) {
        String str = "";
        Date date = new Date(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        str = dateFormat.format(date);
        return str;
    }

    public static String getRealPathFromURI(Context mContext, Uri contentUri) {
        // TODO: get realpath from uri
        String stringPath = null;
        try {
            if (contentUri.getScheme().toString().compareTo("content") == 0) {
                String[] proj = {MediaStore.Images.Media.DATA};
                CursorLoader loader = new CursorLoader(((Activity) mContext), contentUri, proj, null, null, null);
                Cursor cursor = loader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                stringPath = cursor.getString(column_index);
                cursor.close();
            } else if (contentUri.getScheme().compareTo("file") == 0) {
                stringPath = contentUri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringPath;

    }

    public static void blinkAnimation(View view) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        view.startAnimation(anim);
    }

    public static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public static void clearNortifications(Context mContext) {
        NotificationManager nManager = ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE));
        nManager.cancelAll();
    }

    public static void sendEmail(Context mContext, String email) {

        Intent emailSelectorIntent = new Intent(Intent.ACTION_SENDTO);
        emailSelectorIntent.setData(Uri.parse("mailto:"));

        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        emailIntent.setSelector(emailSelectorIntent);

        if (emailIntent.resolveActivity(mContext.getPackageManager()) != null) mContext.startActivity(emailIntent);

    }

    public static void call(Context mContext, String no) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", no, null));
        mContext.startActivity(intent);
    }

    public static void imageShowFullscreenDialog(Context mContext, String url) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.image_fullscreen_dialog);
        TouchImageView ivImage = dialog.findViewById(R.id.ivImage);
        ivImage.setMaxZoom(4f);
        Glide.with(mContext).load(url).into(ivImage);
        dialog.show();
    }

    public static void pauseProgressDialog() {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.cancel();
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    public static String getPolyLineUrl(Context context, LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=" + context.getResources().getString(R.string.googlekey_other);
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.e("PathURL", "====>" + url);
        return url;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    public static void changeStatusBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.purple_200, activity.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.purple_200));
        }
    }


    public static String getCurrentDate2() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(new Date());
        return formattedDate;
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = dateFormat.format(new Date());
        return formattedDate;
    }

    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = dateFormat.format(new Date());
        return formattedDate;
    }

    public static long convertDateIntoMillisecond(String date) {
        String date_ = date;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
            return timeInMilliseconds;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;

    }

    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "getting address...";
        if (context != null) {
            Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder("");

                    for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                    strAdd = strReturnedAddress.toString();
                    Log.w("My Current address", strReturnedAddress.toString());
                } else {
                    strAdd = "No Address Found";
                    Log.w("My Current address", "No Address returned!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                strAdd = "Cant get Address";
                Log.w("My Current address", "Canont get Address!");
            }
        }
        return strAdd;
    }


    public static String getRealPathFromURI(Activity activity, Uri contentUri) {
        //TODO: get realpath from uri
        String stringPath = null;
        try {
            if (contentUri.getScheme().toString().compareTo("content") == 0) {
                String[] proj = {MediaStore.Images.Media.DATA};
                CursorLoader loader = new CursorLoader(activity, contentUri, proj, null, null, null);
                Cursor cursor = loader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                stringPath = cursor.getString(column_index);
                cursor.close();
            } else if (contentUri.getScheme().compareTo("file") == 0) {
                stringPath = contentUri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringPath;
    }


    public static List<String> getPercent() {
        List<String> list = new ArrayList<>();
        list.add("0%");
        list.add("1%");
        list.add("2%");
        list.add("3%");
        list.add("4%");
        list.add("5%");
        list.add("6%");
        list.add("7%");
        list.add("8%");
        list.add("9%");
        list.add("10%");
        list.add("11%");
        list.add("12%");
        list.add("13%");
        list.add("14%");
        list.add("15%");
        list.add("16%");
        list.add("17%");
        list.add("18%");
        list.add("19%");
        list.add("20%");
        list.add("21%");
        list.add("22%");
        list.add("23%");
        list.add("24%");
        list.add("25%");
        list.add("26%");
        list.add("27%");
        list.add("28%");
        list.add("29%");
        list.add("30%");
        list.add("31%");
        list.add("32%");
        list.add("33%");
        list.add("34%");
        list.add("35%");
        list.add("36%");
        list.add("37%");
        list.add("38%");
        list.add("39%");
        list.add("40%");
        list.add("41%");
        list.add("42%");
        list.add("43%");
        list.add("44%");
        list.add("45%");
        list.add("46%");
        list.add("47%");
        list.add("48%");
        list.add("49%");
        list.add("50%");
        list.add("51%");
        list.add("52%");
        list.add("53%");
        list.add("54%");
        list.add("55%");
        list.add("56%");
        list.add("57%");
        list.add("58%");
        list.add("59%");
        list.add("60%");
        list.add("61%");
        list.add("62%");
        list.add("63%");
        list.add("64%");
        list.add("65%");
        list.add("66%");
        list.add("67%");
        list.add("68%");
        list.add("69%");
        list.add("70%");
        list.add("71%");
        list.add("72%");
        list.add("73%");
        list.add("74%");
        list.add("75%");
        list.add("76%");
        list.add("77%");
        list.add("78");
        list.add("79%");
        list.add("80%");
        list.add("81%");
        list.add("82%");
        list.add("83%");
        list.add("84%");
        list.add("85%");
        list.add("86%");
        list.add("87%");
        list.add("88%");
        list.add("89%");
        list.add("90%");
        list.add("91%");
        list.add("92%");
        list.add("93%");
        list.add("94%");
        list.add("95%");
        list.add("96%");
        list.add("97%");
        list.add("98%");
        list.add("99%");
        list.add("100%");

        return list;
    }



}
