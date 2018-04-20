package com.example.android.placesearch.presentor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;

import com.example.android.placesearch.helper_classes.DownloadUrl;
import com.example.android.placesearch.helper_classes.MyItem;
import com.example.android.placesearch.helper_classes.WriteData;
import com.example.android.placesearch.model.Geometry;
import com.example.android.placesearch.model.PlaceInfo;
import com.example.android.placesearch.model.PlaceResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JSONDataHelper {

    private ClusterManager<MyItem> mClusterManager;
    private Context context;
    private GoogleMap mMap;
    private EditText editText;

    JSONDataHelper(ClusterManager<MyItem> clusterManager, Context context, GoogleMap mMap, EditText editText){
        mClusterManager = clusterManager;
        this.context = context;
        this.mMap = mMap;
        this.editText = editText;
    }

    void getJSONData(String _location, String proximity, String _searchString, String sensor, String key) {
        Log.e("", "<------------------getJSONData running-------------->");
        mMap.clear();
        Retrofit retrofit = new Retrofit.Builder()
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

    private void showNearbyPlace(List<PlaceInfo> nearbyPlaces) {
        Log.e("Tracing", "<----------------- showing Nearby Places ------------------>");
        Log.e("String Value ", String.valueOf(nearbyPlaces.size()));
        WriteData writeData = new WriteData(context);
        mClusterManager.clearItems();
        for (int i = 0; i < nearbyPlaces.size(); i++) {
            PlaceInfo googlePlaces = nearbyPlaces.get(i);
            String placeName = googlePlaces.getPlaceName();
            Log.e("Place Name", googlePlaces.getPlaceName());
            String vicinity = googlePlaces.getVicinity();
            Log.e("Vicinity Name", googlePlaces.getVicinity());
            Geometry geometry = googlePlaces.getGeometry();
            double lat = geometry.getLocation().getLat();
            Log.e("Latitude", String.valueOf(lat));
            double lng = geometry.getLocation().getLng();
            Log.e("Longitude", String.valueOf(lng));
            String icon = googlePlaces.getIcon();
            String rating = String.valueOf(googlePlaces.getRating());
            LatLng latLng = new LatLng(lat, lng);
            Log.e("Rating ------->", "-------------------------------->"+rating);
//            writeToRealm(placeName,vicinity,lat,lng,googlePlaces.getIcon(),googlePlaces.getRating());
            Log.e("Get Icon", googlePlaces.getIcon());
//            Picasso.get().load(googlePlaces.getIcon()).into(imageView);
            mClusterManager.addItem(new MyItem(lat, lng, placeName, vicinity, icon,rating));
            writeData.writeToRealm(editText.getText().toString(),placeName,vicinity,lat,lng,icon,rating);
        }
        mClusterManager.cluster();
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,MainActivity.DEFAULT_ZOOM));
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {
                mMap.moveCamera(CameraUpdateFactory.zoomIn());
                return false;
            }
        });

    }
}
