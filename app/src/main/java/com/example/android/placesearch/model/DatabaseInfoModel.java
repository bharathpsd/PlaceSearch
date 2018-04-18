package com.example.android.placesearch.model;

import io.realm.RealmObject;

public class DatabaseInfoModel extends RealmObject{
    private String name;
    private String vicinity;
    private String lat;
    private String lng;
    private double rating;
    private String icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "DatabaseInfoModel{" +
                "name='" + name + '\'' +
                ", vicinity='" + vicinity + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", rating=" + rating +
                ", icon='" + icon + '\'' +
                '}';
    }
}
