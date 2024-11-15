package com.tech.amanah.Utils;

import android.content.Context;
import android.util.Log;


import com.google.android.gms.maps.model.LatLng;
import com.tech.amanah.R;


public class BaseClass {

    public static BaseClass get() {
        return new BaseClass();
    }


    public String getPolyLineUrl(Context context, LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=" + context.getResources().getString(R.string.googlekey_other);
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.e("PathURL","====>"+url);
        return url;
    }

}
