package com.example.android.placesearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DownloadUrl{
    String BASE_URL = "https://maps.googleapis.com";

    @GET("/maps/api/place/nearbysearch/json")
    Call<PlaceResponse> getPlaceDetailsForQuery(@Query("location") String location,
                                                @Query("radius") String radius,
                                                @Query("keyword") String searchString,
                                                @Query("sensor") String senor,
                                                @Query("key") String key);
        }