package com.example.android.placesearch.helper_classes;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class MyItem implements ClusterItem, com.google.maps.android.clustering.ClusterItem {
    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private String mIcon;
    private double mLat;
    private double mLng;
    private String rating;

//    public MyItem(double lat, double lng) {
//        mPosition = new LatLng(lat, lng);
//    }

    public MyItem(double lat, double lng, String title, String snippet, String icon, String rating) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        mIcon = icon;
        mLat = lat;
        mLng = lng;
        this.rating = rating;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getPlaceTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public String getmIcon() {
        return mIcon;
    }

    public String getRating(){return rating;}


    public double getmLat() {
        Log.e("Latitude in:", String.valueOf(mLat));
        return mLat;
    }

    public double getmLng() {
        Log.e("Longitude in:", String.valueOf(mLng));
        return mLng;
    }
}