package com.example.android.placesearch;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.placesearch.model.Geometry;
import com.example.android.placesearch.model.PlaceInfo;
import com.example.android.placesearch.model.PlaceResponse;
import com.example.android.placesearch.view.CustomInfoWindowAdapter;
import com.example.android.placesearch.view.GridRecycler;
import com.example.android.placesearch.view.InfoActivity;
import com.example.android.placesearch.view.RecyclerFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private String searchString = "food";

    //permissions
    private static final String fine_location = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String coarse_location = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_REQUESTED_PERMISSION_CODE = 1234;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    public static float DEFAULT_ZOOM = 5f;
    private static int PROXIMITY_RADIUS = 500000;
    FusedLocationProviderClient mFusedLocationProviderClient;

    //widget
    AutoCompleteTextView editText;
    ImageView imageView;
    FloatingActionButton fab_main,fab_grid,fab_maps;
    private static boolean fab_visibility;
    Animation show_fab,hide_fab;

    //Realmdatabase
    Realm realm;

    //map variables
    private boolean mLocationPermissionGranted = false;
    private GoogleMap mMap = null;
    private double latitude, longitude;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private GoogleApiClient client;
    private Marker currentLocationMarker;
    private ClusterManager<MyItem> mClusterManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);
        editText = findViewById(R.id.input_search);
        imageView = findViewById(R.id.imageview_main);
        fab_main = findViewById(R.id.fab_main);
        fab_grid = findViewById(R.id.fab_1);
        fab_maps = findViewById(R.id.fab_2);
        show_fab = AnimationUtils.loadAnimation(this,R.anim.show_fab);
        hide_fab = AnimationUtils.loadAnimation(this,R.anim.hide_fab);
        fab_visibility = false;
        //Checking whether the Google Play Services are available or not
        if (googlePlayServicesAvailability()) {
            Toast.makeText(this, "Google Play Services are Available", Toast.LENGTH_SHORT).show();
            Log.e("##################", "Play Services are available");
            getPermissionLocation();
        } else {
            Toast.makeText(this, "Something is wrong", Toast.LENGTH_SHORT).show();
            Log.e("##################", "Play Services not available");
        }


        fab_main.setImageResource(R.drawable.settings);
        fab_main.setBackgroundColor(Color.parseColor("#448AFF"));
        fab_grid.setImageResource(R.drawable.grid_layout);
        fab_maps.setImageResource(R.drawable.maps);
        getDeviceLocation();
//        loadRecyclerFragment(latitude,longitude,searchString);
        Bundle bundle = new Bundle();
        bundle.putString("search_string", "food");
        RecyclerFragment recyclerFragment = new RecyclerFragment();
        recyclerFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, recyclerFragment);
        transaction.commit();
        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fab_visibility == false){
                    fab_visibility = true;
                    fab_grid.setVisibility(View.VISIBLE);
                    fab_maps.setVisibility(View.VISIBLE);
                    fab_maps.startAnimation(show_fab);
                    fab_grid.startAnimation(show_fab);
                } else {
                    fab_visibility = false;
                    fab_grid.setVisibility(View.INVISIBLE);
                    fab_maps.setVisibility(View.INVISIBLE);
                    fab_maps.startAnimation(hide_fab);
                    fab_grid.startAnimation(hide_fab);
                }


            }
        });

        fab_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,GridRecycler.class);
                intent.putExtra("search_string",editText.getText().toString());
                Log.e("Latitude","<------------------" + String.valueOf(latitude));
                intent.putExtra("pre_lat",latitude);
                intent.putExtra("pre_lng",longitude);
                startActivityForResult(intent,1);
            }
        });



    }

    public void loadRecyclerFragment(double lat,double lng,String l){
        Log.e("Calling Load Fragment","<---------------------------------Recycler--------->");
        Bundle bundle = new Bundle();
        bundle.putString("search_string", l);
        RecyclerFragment recyclerFragment = new RecyclerFragment();
        recyclerFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, recyclerFragment);
        Log.e("Calling Load Fragment","<---------------------------------Commit--------->");
        transaction.commit();
    }


    private void init() {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN ||
                        event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    //execute method for searching
                    geoLocate();
                    return true;
                }
                return false;
            }
        });

        hideSoftKeyBoard(this);
    }

    public void setLongitude(double longitude_current, double latitude_current) {
        longitude = longitude_current;
        latitude = latitude_current;
        loadRecyclerFragment(latitude_current,longitude_current,"food");
    }

    public void getDeviceLocation() {
        Log.e("#########", "Getting Device Current Location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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


    private void geoLocate() {
        Log.e("########", "GeoLocating");
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 7));
        searchString = editText.getText().toString();
        String location;
        String key = "AIzaSyCISqMTancgO02iHQ-VRE8praCFcH-1uqQ";
//        String url = getUrl(searchString,latitude,longitude);
        if (latitude != 0 && longitude != 0) {
            location = String.valueOf(latitude) + "," + String.valueOf(longitude);
            loadRecyclerFragment(latitude,longitude,searchString);
            getJSONData(location, String.valueOf(PROXIMITY_RADIUS), searchString, "true", key);
            hideSoftKeyBoard(MainActivity.this);
            Toast.makeText(this, "Showing nearby " + searchString, Toast.LENGTH_LONG).show();
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
        hideSoftKeyBoard(this);
    }

    /**
     * If there are Google Play services in the emulator or the device we run this app on
     * then return true and display toast to make sure user know that the map is ready
     * else return false
     *
     * @return false
     */
    public boolean googlePlayServicesAvailability() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Cannot go to Play Services", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.e("##################", "Map is Ready to Load");
        mMap = googleMap;
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
        mClusterManager = new ClusterManager<>(this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setRenderer(new ManageClusterManager(this, mMap, mClusterManager));
        mClusterManager.getMarkerCollection()
                .setOnInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(getApplicationContext())));

        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());


        mClusterManager.setOnClusterItemInfoWindowClickListener(
                new ClusterManager.OnClusterItemInfoWindowClickListener<MyItem>() {
                    @Override
                    public void onClusterItemInfoWindowClick(MyItem stringClusterItem) {
                        Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                        intent.putExtra("Address", stringClusterItem.getPlaceTitle());
                        intent.putExtra("Vicinity", stringClusterItem.getSnippet());
                        intent.putExtra("Latitude", stringClusterItem.getmLat());
                        intent.putExtra("Longitude", stringClusterItem.getmLng());
                        intent.putExtra("Icon",stringClusterItem.getmIcon());
                        intent.putExtra("Rating", stringClusterItem.getRating());
                        startActivity(intent);
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
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
//            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }
    }

    private void initMap() {
        Log.e("##################", "Initializing Map");
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(MainActivity.this);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void getPermissionLocation() {
        Log.e("##################", "Getting Permissions");
        String[] permissions = {fine_location, coarse_location};
        if (ContextCompat.checkSelfPermission(getApplicationContext(), fine_location) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), coarse_location) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_REQUESTED_PERMISSION_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_REQUESTED_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
                    initMap();
                }


        }
    }

    private void hideSoftKeyBoard(Activity activity) {
        editText.clearFocus();
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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

    private void getJSONData(String _location, String proximity, String _searchString, String sensor, String key) {
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
        WriteData writeData = new WriteData(this);
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
            Picasso.get().load(googlePlaces.getIcon()).into(imageView);
            mClusterManager.addItem(new MyItem(lat, lng, placeName, vicinity, icon,rating));
            writeData.writeToRealm(editText.getText().toString(),placeName,vicinity,lat,lng,icon,rating);
        }
        loadRecyclerFragment(latitude,longitude,searchString);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}