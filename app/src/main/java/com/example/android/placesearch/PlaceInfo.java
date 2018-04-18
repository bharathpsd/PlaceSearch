package com.example.android.placesearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceInfo {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("vicinity")
    @Expose
    private String vicinity;

    @SerializedName("geometry")
    @Expose
    private String geometry;


    @SerializedName("lng")
    @Expose
    private String lng;

    public String getGeometry() {
        return geometry;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceName() {
        return name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public String getLng() {
        return lng;
    }


//    public BitmapDescriptor getIcon() {
////        return icon;
//    }
}
