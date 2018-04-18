package com.example.android.placesearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LatLngClass {
    @SerializedName("lat")
    @Expose
    double lat;

    @SerializedName("lng")
    @Expose
    double lng;

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
