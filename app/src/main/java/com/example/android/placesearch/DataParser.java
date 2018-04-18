package com.example.android.placesearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    List<PlaceInfo> placesList = new ArrayList<>();
    private void getPlace(JSONObject googlePlaceObject){
        Log.e("Tracing","<----------------- Get Place ------------------>");
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        String icon="";
        try {
            if (!googlePlaceObject.isNull("name")) {
                placeName = googlePlaceObject.getString("name");
                Log.e("Name",placeName);
            }
            if (!googlePlaceObject.isNull("vicinity")) {
                vicinity = googlePlaceObject.getString("vicinity");
                Log.e("Vicinity",vicinity);
            }
            latitude = googlePlaceObject.getJSONObject("geometry").getJSONObject("location").getString("lat");
            Log.e("Latitude",latitude);
            longitude = googlePlaceObject.getJSONObject("geometry").getJSONObject("location").getString("lng");
            Log.e("Longitude",longitude);
            if(!googlePlaceObject.isNull("icon")){
                icon = googlePlaceObject.getString("icon");
            }


//            placesList.add(new PlaceInfo(placeName,vicinity,latitude,longitude,icon));
        }catch (JSONException e) {
            Log.e("JSON Exception",e.getMessage());
        }
    }


    private void getPlaces(JSONArray jsonArray){
        Log.e("Tracing","<----------------- get Palces ------------------>");
        int count = jsonArray.length();
        try {
        for(int i=0;i<count;i++){
                getPlace((JSONObject) jsonArray.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<PlaceInfo> parse(String jsonData){
        Log.e("Tracing","<----------------- Parse ------------------>");
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
            Log.e("JSON Array",jsonArray.toString());
            getPlaces(jsonArray);
        } catch (JSONException e) {
            Log.e("JSON Exception",e.getMessage());
        }
        return placesList;
    }
}
