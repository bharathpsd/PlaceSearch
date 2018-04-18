package com.example.android.placesearch;

import com.example.android.placesearch.Geometry;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class PlaceInfo {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("vicinity")
    @Expose
    private String vicinity;

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;


    public Geometry getGeometry() {
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


    public void setGeometry(Geometry geometry){
        this.geometry = geometry;
    }


//    public BitmapDescriptor getIcon() {
////        return icon;
//    }
}
