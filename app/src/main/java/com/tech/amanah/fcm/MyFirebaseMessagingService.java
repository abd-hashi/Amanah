package com.tech.amanah.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.MusicManager;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.activities.LoginActivity;
import com.tech.amanah.activities.MyOrdersAct;
import com.tech.amanah.activities.SelectService;
import com.tech.amanah.activities.SplashActivity;
import com.tech.amanah.shops.activities.ShopHomeAct;
import com.tech.amanah.shops.activities.ShopOrdersAct;
import com.tech.amanah.taxiservices.activities.TaxiHomeAct;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private NotificationChannel mChannel;
    private NotificationManager notificationManager;
    private MediaPlayer mPlayer;
    Intent intent;
    SharedPref sharedPref;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sharedPref = SharedPref.getInstance(this);

        Log.e(TAG, "fsfsdfd:" + remoteMessage.getData());

        if (remoteMessage.getData().size() > 0) {

            Map<String, String> data = remoteMessage.getData();

            try {

                String title = "", key = "", status = "", noti_type = "";

                JSONObject object = new JSONObject(data.get("message"));

                try {
                    noti_type = object.getString("noti_type");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if(AppConstant.TAXI_DRIVER.equals(noti_type)) {
                        status = object.getString("booking_status");
                    } else {
                        status = object.getString("status");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    key = object.getString("key");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.e("fasdfsadfsfs","key = " + key);
                Log.e("fasdfsadfsfs","noti_type = " + noti_type);
                Log.e("fasdfsadfsfs","status = " + status);

                if ("DEV_FOOD".equals(noti_type)) {
                    title = "AmanahUser";
                    if ("Confirmed".equals(status) ||
                            "Accept".equals(status) ||
                            "Pickup".equals(status) ||
                            "Delivered".equals(status)) {
                        key = object.getString("key");
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData=====", object.toString());
                        intent1.putExtra("object", object.toString());
                        sendBroadcast(intent1);
                    }
                } else if (AppConstant.TAXI_DRIVER.equals(noti_type)) {

                    if ("Accept".equals(status)) {
                        title = "New Booking Request";
                        key = object.getString("key");
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData ===== ", object.toString());
                        intent1.putExtra("status", "Accept");
                        intent1.putExtra("message", object+"");
                        sendBroadcast(intent1);
                    }

                    else if ("Cancel".equals(status)) {
                        // title = object.getString("title");
                        title = "New Booking Request";
                        key = object.getString("key");
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData ===== ", object.toString());
                        intent1.putExtra("status", "Cancel");
                        sendBroadcast(intent1);
                    } else if ("Start".equals(status)) {
                        title = "New Booking Request";
                        key = object.getString("key");
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData ===== ", object.toString());
                        intent1.putExtra("status", "Start");
                        sendBroadcast(intent1);
                    } else if ("End".equals(status)) {
                        title = "New Booking Request";
                        key = object.getString("key");
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData ===== ", object.toString());
                        intent1.putExtra("status", "End");
                        sendBroadcast(intent1);
                    } else if ("Arrived".equals(status)) {
                        title = "New Booking Request";
                        key = object.getString("key");
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData ===== ", object.toString());
                        intent1.putExtra("status", "Arrived");
                        sendBroadcast(intent1);
                    }

                    else if ("ChangeAmount".equals(status)) {
                        title = "Booking Updated by admin";
                        key = "Booking Updated by admin";
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData=====", object.toString());
                        intent1.putExtra("status", "ChangeAmount");
                        sendBroadcast(intent1);
                    }

                    else if ("admin_cancel".equals(status)) {
                        title = "Booking Cancel by admin";
                        key = "Booking Cancel by admin";
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData=====", object.toString());
                        intent1.putExtra("status", "admin_cancel");
                        sendBroadcast(intent1);
                    }


                    else if ("New Driver Assign".equals(status)) {
                        title = "New Driver Assign";
                        key = "New Driver assign  by admin for this booking";
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData=====", object.toString());
                        intent1.putExtra("status", "New Driver Assign");
                        sendBroadcast(intent1);
                    }

                    else if ("New Driver Booking".equals(status)) {
                        title = "New Driver Assign";
                        key = "New Driver assign  by admin for this booking";
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData=====", object.toString());
                        intent1.putExtra("status", "New Driver Booking");
                        sendBroadcast(intent1);
                    }

                    else if ("ChatMsg".equals(status)) {
                        title = key;
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData=====", object.toString());
                        intent1.putExtra("status", "Chat");
                        sendBroadcast(intent1);
                    }


                    else if ("".equals(status)) {
                        title = key;
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData=====", object.toString());
                        intent1.putExtra("status", "Chat");
                        sendBroadcast(intent1);
                    }


                  /*  else if ("admin_delete".equals(status)) {
                        title = "This user deleted by admin";
                        key = "This user deleted by admin";
                       //Intent intent1 = new Intent("Job_Status_Action");
                     //   Log.e("SendData=====", object.toString());
                      //  intent1.putExtra("status", "admin_cancel");
                      //  sendBroadcast(intent1);
                        sharedPref.clearAllPreferences();
                        startActivity(new Intent(this, SplashActivity.class));
                    }*/


                }

                else if ("admin_delete".equals(status)) {
                    title = "Your account deleted by admin";
                    key = "Your account deleted by admin";
                    //Intent intent1 = new Intent("Job_Status_Action");
                    //   Log.e("SendData=====", object.toString());
                    //  intent1.putExtra("status", "admin_cancel");
                    //  sendBroadcast(intent1);
                    displayCustomBlockNotify(status, title, key, object.toString());
                    sharedPref.clearAllPreferences();
                    startActivity(new Intent(this, SplashActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }


                else if ("Deactive".equals(status)) {
                    title = "Your account deactiveted by admin";
                    key = "Your account deactiveted by admin";
                    //Intent intent1 = new Intent("Job_Status_Action");
                    //   Log.e("SendData=====", object.toString());
                    //  intent1.putExtra("status", "admin_cancel");
                    //  sendBroadcast(intent1);
                    displayCustomBlockNotify(status, title, key, object.toString());
                    sharedPref.clearAllPreferences();
                    startActivity(new Intent(this, SplashActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }

                else if ("Block".equals(status)) {
                    title = "Your account blocked by admin";
                    key = "Your account blocked by admin";
                    //Intent intent1 = new Intent("Job_Status_Action");
                    //   Log.e("SendData=====", object.toString());
                    //  intent1.putExtra("status", "admin_cancel");
                    //  sendBroadcast(intent1);
                    displayCustomBlockNotify(status, title, key, object.toString());
                    sharedPref.clearAllPreferences();
                    startActivity(new Intent(this, SplashActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }


                if (sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
                    if (AppConstant.TAXI_DRIVER.equals(noti_type)) {
                        displayCustomTaxiNotify(status, title, key, object.toString());
                    }

                   else if ("USER".equals(noti_type)) {
                        displayCustomUserNotify(status, title, object.getString("message"), object.toString());
                    }

                    else if ("ShopUser".equals(noti_type)) {
                         title = "New Booking Order";
                        displayCustomUserShopUserNotify(status, title,  object.getString("message"), object.toString());
                    }


                    else if ("SHOP".equals(noti_type)) {
                        displayCustomUserShopNotify(status, title,  object.getString("message"), object.toString());
                    }
                    else {
                        if ("Confirmed".equals(status)) {
                            displayCustomNotificationForOrders(status, title, getString(R.string.order_confirmed_text), object.toString());
                        } else if ("Accept".equals(status)) {
                            displayCustomNotificationForOrders(status, title, getString(R.string.order_accept_text), object.toString());
                        } else if ("Pickup".equals(status)) {
                            displayCustomNotificationForOrders(status, title, getString(R.string.order_pickup_text), object.toString());
                        } else if ("Delivered".equals(status)) {
                            displayCustomNotificationForOrders(status, title, getString(R.string.order_devlivered_text), object.toString());
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    private void displayCustomBlockNotify(String status, String title, String msg, String data) {

        intent = new Intent();

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_MUTABLE);
        String channelId = "1";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Cloud Messaging Service",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(getNotificationId(), notificationBuilder.build());

    }


    private void displayCustomTaxiNotify(String status, String title, String msg, String data) {

        intent = new Intent(this, TaxiHomeAct.class);
        intent.putExtra("type", "dialog");
        intent.putExtra("data", data);
        intent.putExtra("object", data);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_MUTABLE);
        String channelId = "1";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Cloud Messaging Service",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(getNotificationId(), notificationBuilder.build());

    }

    private void displayCustomUserNotify(String status, String title, String msg, String data) {

        intent = new Intent(this, SelectService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_MUTABLE);
        String channelId = "1";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Cloud Messaging Service",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(getNotificationId(), notificationBuilder.build());

    }

    private void displayCustomUserShopUserNotify(String status, String title, String msg, String data) {

        intent = new Intent(this, ShopOrdersAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_MUTABLE);
        String channelId = "1";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Cloud Messaging Service",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(getNotificationId(), notificationBuilder.build());

    }


    private void displayCustomUserShopNotify(String status, String title, String msg, String data) {

        intent = new Intent(this, ShopHomeAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_MUTABLE);
        String channelId = "1";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Cloud Messaging Service",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(getNotificationId(), notificationBuilder.build());

    }


    private void displayCustomNotificationForOrders(String status, String title, String msg, String data) {

        intent = new Intent(this, MyOrdersAct.class);
        intent.putExtra("type", "dialog");
        intent.putExtra("data", data);
        intent.putExtra("object", data);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_MUTABLE);
        String channelId = "1";

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Cloud Messaging Service",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(getNotificationId(), notificationBuilder.build());

    }

    private static int getNotificationId() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(9000);
    }

}
