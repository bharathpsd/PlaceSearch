package com.example.android.placesearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PlaceResponse {


    @SerializedName("results")
    @Expose
    private ArrayList<PlaceInfo> results;

    @SerializedName("status")
    @Expose
    private String status;


    public ArrayList<PlaceInfo> getResults() {
        return this.results;
    }

    public void setResults(ArrayList<PlaceInfo> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
