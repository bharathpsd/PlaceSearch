package com.example.android.placesearch;

//import android.content.Context;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.placesearch.Geometry;
import com.example.android.placesearch.model.DatabaseInfoModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.CompactOnLaunchCallback;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetPlaces{
    private Realm realm;

    GetPlaces(Context context) {
        initRealm(context);
    }

    private void initRealm(Context context){
        // Initialize Realm
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
    }


    private void writeToRealm(final String placeName, final String vicinity, final double lat, final double lng, final String icon, final double rating) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DatabaseInfoModel info = realm.createObject(DatabaseInfoModel.class);
                info.setName(placeName);
                info.setVicinity(vicinity);
                info.setLat(String.valueOf(lat));
                info.setLng(String.valueOf(lng));
                info.setIcon(icon);
                info.setRating(rating);
            }


        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("Stored Successfully","Realm Database stored these values");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("Storing Error","Realm Database has some error storing the values",error);
            }
        });
    }


}
