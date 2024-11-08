package com.tech.amanah.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser2 {
    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     */
    public String parse2(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        JSONObject distanse;
        String dis="";
        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                distanse = jLegs.getJSONObject(Integer.parseInt("0")).getJSONObject("distance");
                String Aa[] = distanse.getString("text").split(" ");
             //   dis = Aa[0];
                dis = distanse.getString("text");

            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }


        return dis;
    }


}
