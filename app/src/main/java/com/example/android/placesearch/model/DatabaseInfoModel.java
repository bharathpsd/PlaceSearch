package com.example.android.placesearch.model;

import io.realm.RealmObject;

public class DatabaseInfoModel extends RealmObject{
    private String name;
    private String vicinity;
    private String lat;
    private String lng;
    private String rating;
    private String icon;

    private String searchString;

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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setSearchString(String searchString) { this.searchString = searchString; }


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
