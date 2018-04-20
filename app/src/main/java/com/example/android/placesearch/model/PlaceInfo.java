package com.example.android.placesearch.model;

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
    private Geometry geometry;

    @SerializedName("icon")
    @Expose
    private String icon;

    @SerializedName("rating")
    @Expose
    private double rating;

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setGeometry(Geometry geometry){
        this.geometry = geometry;
    }


//    public BitmapDescriptor getIcon() {
////        return icon;
//    }
}
