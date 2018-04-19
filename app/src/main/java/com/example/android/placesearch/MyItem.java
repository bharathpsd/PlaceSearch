package com.example.android.placesearch;

import com.google.android.gms.maps.model.LatLng;

class MyItem implements ClusterItem, com.google.maps.android.clustering.ClusterItem {
    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;

//    public MyItem(double lat, double lng) {
//        mPosition = new LatLng(lat, lng);
//    }

    public MyItem(double lat, double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
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
}