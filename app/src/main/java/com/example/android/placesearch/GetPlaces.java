package com.example.android.placesearch;

//import android.content.Context;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.example.android.placesearch.Geometry;
import com.example.android.placesearch.model.DatabaseInfoModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
    private ClusterManager<MyItem> mClusterManager = null;
    private String googlePlacesData;
    private String _location;
    private String proximity;
    private String _searchString;
    private String sensor;
    private String key;
    private GoogleMap mMap;
    private Realm realm;
    private Retrofit retrofit;
    private JSONObject data;
    private String url;
    private ImageView imageView;
    private Context context;

    GetPlaces(Object[] objects) {
        mMap = (GoogleMap) objects[0];
        _location = (String) objects[1];
        proximity = String.valueOf((int) objects[2]);
        _searchString = (String) objects[3];
        sensor = (String) objects[4];
        key = (String) objects[5];
        imageView = (ImageView) objects[6];
        context = (Context) objects[7];
        initRealm(context);
        getJSONData();
    }

    private void initRealm(Context context){
        // Initialize Realm
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
    }

    private void getJSONData() {
        Log.e("", "<------------------getJSONData running-------------->");
        retrofit = new Retrofit.Builder()
                .baseUrl(DownloadUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DownloadUrl downloadUrlData = retrofit.create(DownloadUrl.class);
        Log.e("", "<------------------Retrofit Data Created-------------->");
        Call<PlaceResponse> call = downloadUrlData.getPlaceDetailsForQuery(_location, proximity, _searchString, sensor, key);
//        Log.e("",call.toString());
        call.enqueue(new Callback<PlaceResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlaceResponse> call, @NonNull Response<PlaceResponse> response) {
//                PlaceResponse placeResponse = response.body();
                showNearbyPlace(response.body().getResults());
            }

            @Override
            public void onFailure(@NonNull Call<PlaceResponse> call, @NonNull Throwable t) {
                Log.e("Received data", "<----------------Nothing Received---------------->");
                Log.e("Error Message", t.getMessage());
            }
        });

    }

    private void showNearbyPlace(List<PlaceInfo> nearbyPlaces){
        Log.e("Tracing","<----------------- showing Nearby Places ------------------>");
        Log.e("String Value ",String.valueOf(nearbyPlaces.size()));
        mClusterManager = new ClusterManager<>(context,mMap);
        for(int i=0;i<nearbyPlaces.size();i++){
            MarkerOptions markerOptions = new MarkerOptions();
            PlaceInfo googlePlaces = nearbyPlaces.get(i);
            String placeName = googlePlaces.getPlaceName();
            String vicinity = googlePlaces.getVicinity();
            Geometry geometry =  googlePlaces.getGeometry();
                double lat =  geometry.getLocation().getLat();
                Log.e("Latitude",String.valueOf(lat));
                double lng =  geometry.getLocation().getLng();
                Log.e("Longitude",String.valueOf(lng));
                LatLng latLng = new LatLng(lat,lng);
            markerOptions.title(placeName);
            markerOptions.position(latLng);
            markerOptions.snippet(vicinity);
            markerOptions.alpha((float) googlePlaces.getRating());
//            writeToRealm(placeName,vicinity,lat,lng,googlePlaces.getIcon(),googlePlaces.getRating());
            Log.e("Get Icon", googlePlaces.getIcon());
            Picasso.get().load(googlePlaces.getIcon()).into(imageView);
            mClusterManager.addItem(new MyItem(lat,lng,placeName,vicinity));
            mMap.addMarker(markerOptions);
        }
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,MainActivity.DEFAULT_ZOOM));
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setRenderer(new ManageClusterManager(context, mMap, mClusterManager));
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
