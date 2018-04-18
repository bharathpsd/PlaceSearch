package com.example.android.placesearch;

//import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
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
    String url;
//    private Context context;
//    protected String doInBackground(Object... objects) {
//        mMap = (GoogleMap) objects[0];
//        url = (String) objects[1];
////        context = (Context) objects[2];
//        realm = (Realm) objects[2];
//        Log.e("Tracing","<----------------- Do in Background------------------>");;
//        DownloadUrl downloadUrl = new DownloadUrl();
//        try {
//            googlePlacesData = downloadUrl.readUrl(url);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Log.e("Google Places Data", googlePlacesData);
//
//        return googlePlacesData;

    GetPlaces(Object[] objects) {
        mMap = (GoogleMap) objects[0];
        _location = (String) objects[1];
        proximity = String.valueOf((int) objects[2]);

        _searchString = (String) objects[3];
        sensor = (String) objects[4];
        key = (String) objects[5];
        getJSONData();
    }

    private void getJSONData(){
        Log.e("","<------------------getJSONData running-------------->");
        retrofit = new Retrofit.Builder()
                .baseUrl(DownloadUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DownloadUrl downloadUrlData = retrofit.create(DownloadUrl.class);
        Log.e("","<------------------Retrofit Data Created-------------->");
        Call<PlaceResponse> call = downloadUrlData.getPlaceDetailsForQuery(_location,proximity,_searchString,sensor,key);
//        Log.e("",call.toString());
        call.enqueue(new Callback<PlaceResponse>() {
            @Override
            public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {
                PlaceResponse placeResponse = response.body();
                showNearbyPlace(response.body().getResults());
            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t) {
                Log.e("Received data","<----------------Nothing Received---------------->");
                Log.e("Error Message", t.getMessage());
            }
        });

//        showNearbyPlace();
    }
//    @Override
//    protected void onPostExecute(String s) {
//        List<PlaceInfo> nearbyPlaceList;
//        Log.e("Tracing","<----------------- Post Execute ------------------>");
//        DataParser dataParser = new DataParser();
//        nearbyPlaceList = dataParser.parse(s);
//        Log.e("Before Calling",String.valueOf(nearbyPlaceList.size()));
//        showNearbyPlace(nearbyPlaceList);

//    }

    private void showNearbyPlace(List<PlaceInfo> nearbyPlaces){
        Log.e("Tracing","<----------------- showing Nearby Places ------------------>");
        Log.e("String Value ",String.valueOf(nearbyPlaces.size()));
        double lat = 0,lng = 0;
        LatLng latLng = null;
        for(int i=0;i<nearbyPlaces.size();i++){
            MarkerOptions markerOptions = new MarkerOptions();
            PlaceInfo googlePlaces = nearbyPlaces.get(i);
//            Log.e("Tracing",nearbyPlaces.get(i).toString());
//            Log.e("Data:" , nearbyPlaces.toString());
            String placeName = googlePlaces.getPlaceName();
            String vicinity = googlePlaces.getVicinity();
            String geometry =  googlePlaces.getGeometry();
            try {
                JSONObject geometry_json = new JSONObject(geometry);
                JSONArray location = geometry_json.getJSONArray("location");
                lat =  Double.parseDouble(location.getString(0));
                 lng =  Double.parseDouble(location.getString(1));
                latLng = new LatLng(lat,lng);
            } catch (JSONException e) {
                e.printStackTrace();
            }


//            Bitmap bmp = Picasso.get().load(googlePlaces.getIcon());

            markerOptions.position(latLng);
            markerOptions.title(placeName);
            markerOptions.snippet(vicinity);
//            markerOptions.icon(googlePlaces.getIcon());
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,MainActivity.DEFAULT_ZOOM));
//            mClusterManager = new ClusterManager<MyItem>(context, mMap);
            mMap.setOnCameraIdleListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MyItem offsetItem = new MyItem(lat, lng);
            mClusterManager.addItem(offsetItem);
//            mClusterManager.setRenderer(new ClusterRenderer<MyItem>(context,mMap) );
        }


    }

}
