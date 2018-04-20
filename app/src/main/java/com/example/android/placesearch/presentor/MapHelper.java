package com.example.android.placesearch.presentor;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.placesearch.view.MainActivity;
import com.example.android.placesearch.helper_classes.ManageClusterManager;
import com.example.android.placesearch.helper_classes.MyItem;
import com.example.android.placesearch.view.CustomInfoWindowAdapter;
import com.example.android.placesearch.view.InfoActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;

import io.realm.Realm;

public class MapHelper extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Context context;
    private Realm realm;
    private MainActivity mainActivity;
    RecyclerView recyclerView;

    public MapHelper(Context context, RecyclerView recyclerView){
        this.context = context;
        mainActivity = new MainActivity();
        this.recyclerView = recyclerView;
    }


    private boolean mLocationPermissionGranted = false;
    //permissions
    private static final String fine_location = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String coarse_location = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_REQUESTED_PERMISSION_CODE = 1234;
//    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    public static float DEFAULT_ZOOM = 5f;
    private static int PROXIMITY_RADIUS = 500000;
    FusedLocationProviderClient mFusedLocationProviderClient;

    //map variables
    public GoogleMap mMap = null;
    public double latitude;
    public double longitude;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private GoogleApiClient client;
    private Marker currentLocationMarker;
    private ClusterManager<MyItem> mClusterManager = null;


    public void setLongitude(double longitude_current, double latitude_current) {
        longitude = longitude_current;
        latitude = latitude_current;
//        mainActivity.loadRecyclerFragment("food");
    }

    public void getDeviceLocation() {
        Log.e("#########", "Getting Device Current Location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.e("#########", "Location Found");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                            setLongitude(currentLocation.getLongitude(), currentLocation.getLatitude());
                            Log.e("My Latitude : ", String.valueOf(currentLocation.getLatitude()));
                            Log.e("My Longitude : ", String.valueOf(currentLocation.getLongitude()));
                        } else {
                            Log.e("#########", "Couldn't find location");
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("#########", "Security Exception");
        }
    }


    private void geoLocate(EditText editText) {
        JSONDataHelper jsonDataHelper = new JSONDataHelper(mClusterManager,context,mMap,editText);
        Log.e("########", "GeoLocating");
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 7));
        String searchString = editText.getText().toString();
        String location;
        String key = "AIzaSyCISqMTancgO02iHQ-VRE8praCFcH-1uqQ";
//        String url = getUrl(searchString,latitude,longitude);
        if (latitude != 0 && longitude != 0) {
            location = String.valueOf(latitude) + "," + String.valueOf(longitude);
//            mainActivity.loadRecyclerView(searchString);
            jsonDataHelper.getJSONData(location, String.valueOf(PROXIMITY_RADIUS), searchString, "true", key);
            Toast.makeText(context, "Showing nearby " + searchString, Toast.LENGTH_LONG).show();
            mainActivity.loadRecyclerView(searchString,recyclerView);
        } else {
            getDeviceLocation();
        }
    }



    private void moveCamera(LatLng latlng, float zoom, String title) {
        Log.e("#################", "Moving Camera to Latlong Object");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions().position(latlng).title(title);
            mMap.addMarker(options);
        }
    }

    /**
     * If there are Google Play services in the emulator or the device we run this app on
     * then return true and display toast to make sure user know that the map is ready
     * else return false
     *
     * @return false
     */
    public boolean googlePlayServicesAvailability(int ERROR_DIALOG_REQUEST) {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(context);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog((Activity) context, isAvailable, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(context, "Cannot go to Play Services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * This is an override method called when we create the fragment and
     * call getMapAsync() method from the fragment
     * This method has a parameter which is the Google map object
     * This is an override method called when we create the fragment and
     * call getMapAsync() method from the fragment
     * This method has a parameter which is the Google map object
     */
    public void mapReady(GoogleMap googleMap, EditText editText) {
        Toast.makeText(context, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.e("##################", "Map is Ready to Load");
        mMap = googleMap;
        client = new GoogleApiClient.Builder(context).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
        mClusterManager = new ClusterManager<>(context, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setRenderer(new ManageClusterManager(context, mMap, mClusterManager));
        mClusterManager.getMarkerCollection()
                .setOnInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(context)));
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

        mClusterManager.setOnClusterItemInfoWindowClickListener(
                new ClusterManager.OnClusterItemInfoWindowClickListener<MyItem>() {
                    @Override
                    public void onClusterItemInfoWindowClick(MyItem stringClusterItem) {
                        Intent intent = new Intent(context, InfoActivity.class);
                        intent.putExtra("Address", stringClusterItem.getPlaceTitle());
                        intent.putExtra("Vicinity", stringClusterItem.getSnippet());
                        intent.putExtra("Latitude", stringClusterItem.getmLat());
                        intent.putExtra("Longitude", stringClusterItem.getmLng());
                        intent.putExtra("Icon",stringClusterItem.getmIcon());
                        intent.putExtra("Rating", stringClusterItem.getRating());
                        context.startActivity(intent);
                    }
                });

        mMap.setOnInfoWindowClickListener(mClusterManager);

        /*
         * If there is the permission we have to show our location on the map.
         * To do that, we have to setMyLocationEnabled(true).
         * As the name suggests it sets the location on the map
         */
        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
//            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init(editText);
        }
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void init(final EditText editText) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN ||
                        event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    //execute method for searching
                    geoLocate(editText);
                    return true;
                }
                return false;
            }
        });
    }


    public boolean getPermissionLocation() {
        Log.e("##################", "Getting Permissions");
        String[] permissions = {fine_location, coarse_location};
        if (ContextCompat.checkSelfPermission(context, fine_location) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(context, coarse_location) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions((Activity) context, permissions, LOCATION_REQUESTED_PERMISSION_CODE);
            }
        } else {
            ActivityCompat.requestPermissions((Activity) context, permissions, LOCATION_REQUESTED_PERMISSION_CODE);
        }

        return mLocationPermissionGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MainActivity mainActivity = new MainActivity();
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_REQUESTED_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.e("##################", "Permission Failed");
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    Log.e("##################", "Permission Success");
                    //initialize map
                    mainActivity.initMap();
                }
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
        Log.d("lat = ", "" + latitude);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.snippet("My Present Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(7));

        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }
    }


}
