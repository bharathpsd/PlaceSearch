package com.example.android.placesearch;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
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
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final int ERROR_DIALOG_REQUEST = 9001;

    //permissions
    private static final String fine_location = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String coarse_location = Manifest.permission.ACCESS_COARSE_LOCATION;

    //map variables
    private boolean mLocationPermissionGranted = false;
    private static final int LOCATION_REQUESTED_PERMISSION_CODE = 1234;
    private GoogleMap mMap = null;
    FusedLocationProviderClient mFusedLocationProviderClient;
    public static float DEFAULT_ZOOM = 5f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));
    private static int PROXIMITY_RADIUS = 500000;
    private double latitude,longitude;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Realm realm;
    private GoogleApiClient client;
    private Marker currentLocationmMarker;
    private ClusterManager<MyItem> mClusterManager = null;
    //widget
    AutoCompleteTextView editText;
    ImageView imageView;
    //places
    private PlaceAutoCompleteAdapter mPlaceAutoCompleteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.input_search);
        imageView = findViewById(R.id.imageview_main);
        //Checking whether the Google Play Services are available or not
        if (googlePlayServicesAvailability()) {
            Toast.makeText(this, "Google Play Services are Available", Toast.LENGTH_SHORT).show();
            Log.e("##################", "Play Services are available");
            getPermissionLocation();
        } else {
            Toast.makeText(this, "Something is wrong", Toast.LENGTH_SHORT).show();
            Log.e("##################", "Play Services not available");
        }
        RecyclerFragment recyclerFragment = new RecyclerFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container,recyclerFragment);
        transaction.commit();

    }

    private void init(){

        mPlaceAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this,Places.getGeoDataClient(this),LAT_LNG_BOUNDS,null);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN ||
                        event.getAction() == KeyEvent.KEYCODE_ENTER){
                        //execute method for searching
                    geoLocate();
                    return true;
                }
                return false;
            }
        });

        hideSoftKeyBoard(this);
    }

    public void setLongitude(double longitude_current,double latitude_current){
        longitude = longitude_current;
        latitude = latitude_current;
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
                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM,"My Location");
                            setLongitude(currentLocation.getLongitude(),currentLocation.getLatitude());
                            Log.e("My Latitude : ",String.valueOf(currentLocation.getLatitude()));
                            Log.e("My Longitude : ",String.valueOf(currentLocation.getLongitude()));
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
        Log.e("########","GeoLocating");
        mMap.clear();
        String searchString = editText.getText().toString();
        String location;
        String key = "AIzaSyCISqMTancgO02iHQ-VRE8praCFcH-1uqQ";
        String url = getUrl(searchString,latitude,longitude);
        if(url != null) {
            location = String.valueOf(latitude) + "," + String.valueOf(longitude);
            getJSONData(location,String.valueOf(PROXIMITY_RADIUS),searchString,"true",key);
            hideSoftKeyBoard(MainActivity.this);
            GetPlaces getPlaces = new GetPlaces(getApplicationContext());
            Toast.makeText(this, "Showing nearby " + searchString, Toast.LENGTH_LONG).show();
        } else {
            Log.e("URL", "URL is null");
        }
    }

    private String getUrl(String searchString,double latitude,double longitude){
        if(latitude != 0 && longitude !=0) {
            StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            googlePlaceUrl.append("location=" + latitude + "," + longitude);
            googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);

            for(int i = 0; i < searchString.length(); i++){
                if(searchString.charAt(i) == ' '){
                    searchString.replace(" ","_");
                    searchString = searchString.replaceAll(" ", "_");
                }
            }
            googlePlaceUrl.append("&type=" + searchString);
            googlePlaceUrl.append("&components=country:us");
            googlePlaceUrl.append("&sensor=true");
            googlePlaceUrl.append("&key=AIzaSyCISqMTancgO02iHQ-VRE8praCFcH-1uqQ");
            Log.e("<------- URL --------->", googlePlaceUrl.toString());
            return googlePlaceUrl.toString();
        } else {
            getDeviceLocation();
            return null;
        }
    }

    private void moveCamera(LatLng latlng, float zoom, String title) {
        Log.e("#################", "Moving Camera to Latlong Object");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));

        if(!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions().position(latlng).title(title);
            mMap.addMarker(options);
        }
        hideSoftKeyBoard(this);
    }

    /**
     * If there are Google Play services in the emulator or the device we run this app on
     * then return true and display toast to make sure user know that the map is ready
     * else return false
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
        mClusterManager = new ClusterManager<>(this,mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setRenderer(new ManageClusterManager(this, mMap, mClusterManager));


        if(mMap != null){

//            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//                @Override
//                public View getInfoWindow(Marker marker) {
//                    return null;
//                }
//
//                @Override
//                public View getInfoContents(final Marker marker) {
//                    View v = getLayoutInflater().inflate(R.layout.info_marker,null);
//                    TextView address_name = v.findViewById(R.id.address_vicinity);
//                    TextView latlng = v.findViewById(R.id.latlng);
//                    TextView vicinity = v.findViewById(R.id.address);
//                    TextView rating = v.findViewById(R.id.rating_info);
//                    address_name.setText(marker.getTitle());
//                    vicinity.setText(marker.getSnippet());
//                    LatLng latLng = marker.getPosition();
//                    rating.setText(String.valueOf(marker.getAlpha()));
//                    String latitude_longitude = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
//                    latlng.setText(latitude_longitude);
//                    return v;
//                }
//            });
//
//
//            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//                @Override
//                public void onInfoWindowClick(Marker marker) {
//                    Intent intent = new Intent(MainActivity.this,InfoActivity.class);
//                    intent.putExtra("Address",marker.getTitle());
//                    intent.putExtra("Vicinity",marker.getTitle());
//                    String latitude_longitude = String.valueOf(marker.getPosition().latitude) + "," + String.valueOf(marker.getPosition().longitude);
//                    intent.putExtra("Location",latitude_longitude);
//                    startActivity(intent);
//                }
//            });
        }
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
        Log.e("##################","Initializing Map");
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(MainActivity.this);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    private void getPermissionLocation(){
        Log.e("##################","Getting Permissions");
        String[] permissions = {fine_location,coarse_location};
        if(ContextCompat.checkSelfPermission(getApplicationContext(),fine_location) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getApplicationContext(),coarse_location) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,permissions,LOCATION_REQUESTED_PERMISSION_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_REQUESTED_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode){
            case LOCATION_REQUESTED_PERMISSION_CODE :
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    for(int i = 0; i<grantResults.length;i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            Log.e("##################","Permission Failed");
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    Log.e("##################","Permission Success");
                    //initialize map
                    initMap();
                }


        }
    }

    private void hideSoftKeyBoard(Activity activity){
        editText.clearFocus();
        InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
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
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();

        }
        Log.d("lat = ",""+latitude);
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.snippet("My Present Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(7));

        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }

    private void getJSONData(String _location,String proximity,String _searchString,String sensor,String key) {
        Log.e("", "<------------------getJSONData running-------------->");
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

    private void showNearbyPlace(List<PlaceInfo> nearbyPlaces){
        Log.e("Tracing","<----------------- showing Nearby Places ------------------>");
        Log.e("String Value ",String.valueOf(nearbyPlaces.size()));

        for(int i=0;i<nearbyPlaces.size();i++){
            PlaceInfo googlePlaces = nearbyPlaces.get(i);
            String placeName = googlePlaces.getPlaceName();
            String vicinity = googlePlaces.getVicinity();
            Geometry geometry =  googlePlaces.getGeometry();
            double lat =  geometry.getLocation().getLat();
            Log.e("Latitude",String.valueOf(lat));
            double lng =  geometry.getLocation().getLng();
            Log.e("Longitude",String.valueOf(lng));
            LatLng latLng = new LatLng(lat,lng);
//            writeToRealm(placeName,vicinity,lat,lng,googlePlaces.getIcon(),googlePlaces.getRating());
            Log.e("Get Icon", googlePlaces.getIcon());
            Picasso.get().load(googlePlaces.getIcon()).into(imageView);
            mClusterManager.addItem(new MyItem(lat,lng,placeName,vicinity));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}